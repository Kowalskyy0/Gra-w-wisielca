import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class HangmanGame {
    private static final Map<String, String[]> CATEGORIES = new HashMap<>();
    private String wordToGuess;
    private String selectedCategory;
    private int remainingAttempts;
    private int wins;
    private int losses;
    private int totalAttempts;
    private JFrame frame;
    private JTextField guessField;
    private JLabel wordLabel;
    private JLabel resultLabel;
    private JLabel attemptsLabel;
    private JLabel titleLabel;
    private JLabel categoryLabel;
    private JLabel winsLabel;
    private JLabel lossesLabel;
    private JComboBox<String> difficultyBox;
    private JLabel totalAttemptsLabel;

    static {
        CATEGORIES.put("Miesiące", new String[]{"styczeń", "luty", "marzec", "listopad", "grudzień"});
        CATEGORIES.put("Owoce", new String[]{"jabłko", "banan", "wiśnia", "figa", "brzoskwinia"});
        CATEGORIES.put("Zwierzęta", new String[]{"słoń", "żyrafa", "kangur", "lew", "tygrys"});
        CATEGORIES.put("Kolory", new String[]{"czerwony", "niebieski", "zielony", "żółty", "fioletowy"});
        CATEGORIES.put("Miasta Polski", new String[]{"Warszawa", "Kraków", "Gdańsk", "Wrocław", "Poznań"});
        CATEGORIES.put("Sporty", new String[]{"boks", "siatkówka", "koszykówka", "tenis", "pływanie"});
        CATEGORIES.put("Kraje Europy", new String[]{"Polska", "Niemcy", "Francja", "Hiszpania", "Włochy"});
        CATEGORIES.put("Pierwiastki chemiczne", new String[]{"wodór", "tlen", "lit", "węgiel", "fosfor"});
        CATEGORIES.put("Gatunki muzyczne", new String[]{"rock", "pop", "jazz", "klasyka", "elektroniczna"});
        CATEGORIES.put("Marki samochodów", new String[]{"Fiat", "Toyota", "Volkswagen", "Audi", "BMW"});
    }

    public HangmanGame() {
        this.remainingAttempts = 0;
        this.wins = 0;
        this.losses = 0;
        this.totalAttempts = 0;
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        frame = new JFrame("Gra w wisielca");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        Container pane = frame.getContentPane();
        pane.setLayout(new GridLayout(10, 2));

        titleLabel = new JLabel("GRA W WISIELCA", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 18));
        pane.add(titleLabel);
        pane.add(new JLabel(""));

        String[] difficulties = {"Łatwy", "Średni", "Trudny"};
        difficultyBox = new JComboBox<>(difficulties);
        difficultyBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });

        guessField = new JTextField();
        wordLabel = new JLabel();
        resultLabel = new JLabel();
        attemptsLabel = new JLabel();
        categoryLabel = new JLabel();
        winsLabel = new JLabel("Wygrane: 0", SwingConstants.CENTER);
        winsLabel.setForeground(Color.GREEN);
        lossesLabel = new JLabel("Przegrane: 0", SwingConstants.CENTER);
        lossesLabel.setForeground(Color.RED);
        totalAttemptsLabel = new JLabel("Nieudane próby: 0", SwingConstants.CENTER);

        JButton guessButton = new JButton("Zgadnij");
        guessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateGuess(guessField.getText())) {
                    makeGuess(guessField.getText().charAt(0));
                }
                guessField.setText("");
            }
        });

        guessField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (validateGuess(guessField.getText())) {
                        makeGuess(guessField.getText().charAt(0));
                    }
                    guessField.setText("");
                }
            }
        });

        pane.add(new JLabel("Poziom trudności: "));
        pane.add(difficultyBox);
        pane.add(new JLabel("Wpisz literę: "));
        pane.add(guessField);
        pane.add(new JLabel("Słowo do zgadnięcia: "));
        pane.add(wordLabel);
        pane.add(guessButton);
        pane.add(resultLabel);
        pane.add(new JLabel("Pozostałe próby: "));
        pane.add(attemptsLabel);
        pane.add(new JLabel("Kategoria: "));
        pane.add(categoryLabel);
        pane.add(winsLabel);
        pane.add(lossesLabel);
        pane.add(totalAttemptsLabel);

        frame.setVisible(true);

        startTitleAnimation();
        startAttemptsLabelAnimation();
    }

    private void startTitleAnimation() {
        Timer timer = new Timer(500, new ActionListener() {
            boolean toggle = true;

            @Override
            public void actionPerformed(ActionEvent e) {
                titleLabel.setForeground(toggle ? Color.RED : Color.BLUE);
                toggle = !toggle;
            }
        });
        timer.start();
    }

    private void startAttemptsLabelAnimation() {
        Timer timer = new Timer(500, new ActionListener() {
            boolean toggle = true;

            @Override
            public void actionPerformed(ActionEvent e) {
                attemptsLabel.setFont(new Font("Serif", toggle ? Font.BOLD : Font.PLAIN, 14));
                toggle = !toggle;
            }
        });
        timer.start();
    }

    public void startGame() {
        Random rand = new Random();
        String[] categories = CATEGORIES.keySet().toArray(new String[0]);
        selectedCategory = categories[rand.nextInt(categories.length)];
        wordToGuess = CATEGORIES.get(selectedCategory)[rand.nextInt(CATEGORIES.get(selectedCategory).length)];

        wordLabel.setText(new String(new char[wordToGuess.length()]).replace("\0", "*"));

        String difficulty = (String) difficultyBox.getSelectedItem();
        switch (difficulty) {
            case "Łatwy":
                remainingAttempts = wordToGuess.length() + 5;
                break;
            case "Średni":
                remainingAttempts = wordToGuess.length() + 2;
                break;
            case "Trudny":
                remainingAttempts = wordToGuess.length();
                break;
        }

        attemptsLabel.setText(String.valueOf(remainingAttempts));
        resultLabel.setText("");
        categoryLabel.setText(selectedCategory);
    }

    private boolean validateGuess(String guess) {
        if (guess.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Pole nie może być puste");
            return false;
        } else if (guess.length() > 1) {
            JOptionPane.showMessageDialog(frame, "W polu powinna znajdować się tylko jedna litera");
            return false;
        }
        return true;
    }

    private void makeGuess(char guess) {
        StringBuilder guessedWord = new StringBuilder(wordLabel.getText());
        String lowerCaseGuess = String.valueOf(guess).toLowerCase();

        if (wordToGuess.toLowerCase().contains(lowerCaseGuess)) {
            for (int i = 0; i < wordToGuess.length(); i++) {
                if (Character.toLowerCase(wordToGuess.charAt(i)) == lowerCaseGuess.charAt(0)) {
                    guessedWord.setCharAt(i, guess);
                }
            }
            wordLabel.setText(guessedWord.toString());

            if (guessedWord.indexOf("*") == -1) {
                resultLabel.setText("Gratulacje, wygrałeś! Słowem było: " + wordToGuess);
                wins++;
                winsLabel.setText("Wygrane: " + wins);
                JOptionPane.showMessageDialog(frame, "Gratulacje, wygrałeś! Słowem było: " + wordToGuess);
                startGame();
            }
        } else {
            remainingAttempts--;
            totalAttempts++;
            attemptsLabel.setText(String.valueOf(remainingAttempts));

            if (remainingAttempts <= 0) {
                resultLabel.setText("Spróbuj jeszcze raz, słowem było: " + wordToGuess);
                losses++;
                lossesLabel.setText("Przegrane: " + losses);
                JOptionPane.showMessageDialog(frame, "Spróbuj jeszcze raz, słowem było: " + wordToGuess);
                startGame();
            }
        }
        updateStats();
    }

    private void updateStats() {
        totalAttemptsLabel.setText("Nieudane próby: " + totalAttempts);
        winsLabel.setText("Wygrane: " + wins);
        lossesLabel.setText("Przegrane: " + losses);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                HangmanGame game = new HangmanGame();
                game.startGame();
            }
        });
    }
}
