import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Locale;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;



/**
 * Hlavná trieda {@code VotingApplication} obsahuje metódy a atribúty na spustenie a správu volebnej aplikácie.
 * <p>
 * Táto trieda poskytuje grafické rozhranie na administráciu volieb, správu kandidátov a zobrazovanie výsledkov.
 * </p>
 *
 * @author Filip Hromada
 * @version 1.0
 */
import volici.*;
import hlasovanie.*;
import logic.*;

public class VotingApplication {

    /**
     * Hlavný vstupný bod aplikácie, ktorý inicializuje grafické používateľské rozhranie.
     * @param args Argumenty príkazového riadku, ktoré sú pre aplikáciu ignorované.
     */
    public static void main(String[] args) {
        setLookAndFeel();
        SwingUtilities.invokeLater(() -> createSplashScreen());
    }

    // Deklarácie atribútov triedy a metód pre správu kandidátov, validáciu vstupov a správu GUI.
    private static ArrayList<String> candidates = new ArrayList<>(Arrays.asList("Ivan Prvy", "Alexander Pekny")); // Zoznam kandidátov
    private static ButtonGroup candidatesGroup = new ButtonGroup(); // Skupina tlačidiel pre výber kandidátov
    private static JPanel candidatePanel; // Panel pre výber kandidátov

    // Textové polia pre zadávanie údajov voliča.
    private static JTextField txtName = new JTextField(15);
    private static JTextField txtAge = new JTextField(15);
    private static JTextField txtID = new JTextField(15);
    private static JTextField txtZipCode = new JTextField(15);
    private static JTextField txtAddress = new JTextField(15);
    private static JComboBox<String> stateComboBox; // Rozbaľovací zoznam štátov

    /**
     * Validuje, či zadaný reťazec obsahuje iba písmená a medzery.
     * @param name Meno na validáciu.
     * @return true, ak je vstup platný, inak false.
     */
    private static boolean isAlpha(String name) {
        return name.matches("[a-zA-Z\\s]+");
    }

    /**
     * Kontroluje, či je zadaný reťazec číselný.
     * @param str Reťazec na overenie.
     * @return true, ak je reťazec číselný, inak false.
     */
    private static boolean isNumeric(String str) {
        return str.matches("\\d+");
    }

    /**
     * Overí, či adresa zadaná vo formáte "ulica číslo" je platná.
     * @param address Adresa na overenie.
     * @return true, ak je adresa v správnom formáte, inak false.
     */
    private static boolean isValidAddress(String address) {
        return address.matches("\\p{Alpha}+\\s+\\d+");
    }

    /**
     * Overí, či je vek voliča platný na hlasovanie.
     * @param age Vek na validáciu.
     * @throws InvalidVotingAgeException ak je vek menší ako 18 rokov.
     */
    public static void validateVoterAge(int age) throws InvalidVotingAgeException {
        if (age < 18) {
            throw new InvalidVotingAgeException("You must be at least 18 years old to vote.");
        }
    }

    /**
     * Vráti zoznam všetkých dostupných krajín v systéme.
     * @return Pole reťazcov predstavujúcich krajiny.
     */
    private static String[] getAllCountries() {
        Locale[] locales = Locale.getAvailableLocales();
        return Arrays.stream(locales)
                .map(Locale::getDisplayCountry)
                .filter(country -> !country.trim().isEmpty())
                .sorted()
                .distinct()
                .toArray(String[]::new);
    }

    /**
     * Inicializuje a zobrazí grafické používateľské rozhranie aplikácie.
     */
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Voting application 2024");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        JPanel introPanel = createIntroPanel();
        JPanel buttonPanel = createButtonPanel(frame);

        frame.add(introPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    /**
     * Zobrazí dialógové okno s aktuálnymi výsledkami volieb pre administrátora.
     * @param parent Referencia na rodičovské okno.
     */
    private static void showLiveResults(JFrame parent) {
        JDialog resultsDialog = new JDialog(parent, "Live Results", false);
        resultsDialog.setSize(400, 400);
        resultsDialog.setLayout(new BorderLayout());

        JEditorPane resultsPane = new JEditorPane();
        resultsPane.setContentType("text/html");
        resultsPane.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultsPane);
        resultsDialog.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton endResultsButton = new JButton("Stop viewing results");
        buttonPanel.add(endResultsButton);
        resultsDialog.add(buttonPanel, BorderLayout.SOUTH);

        resultsDialog.setLocationRelativeTo(parent);
        resultsDialog.setVisible(true);

        final LiveResultsUpdater updater = new LiveResultsUpdater(resultsPane);
        updater.execute();

        resultsDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                updater.cancel(true);
            }
        });

        endResultsButton.addActionListener(e -> {
            updater.cancel(true);
            resultsDialog.dispose();
            showAdminPanel(parent); // This function will open the Admin Panel
        });
    }

    /**
     * Zobrazí okno pre prihlásenie administrátora.
     * @param parent Referencia na rodičovské okno.
     */
    private static void showAdminLogin(JFrame parent) {
        JDialog loginDialog = new JDialog(parent, "Admin Login", true);
        loginDialog.setLayout(new BorderLayout(10, 10)); // Add some spacing between components

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 10, 10)); // Use GridLayout for even spacing
        inputPanel.setBorder(BorderFactory.createTitledBorder("Login")); // Add a titled border for better organization

        JTextField usernameField = new JTextField();
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5))); 

        inputPanel.add(new JLabel("Username:"));
        inputPanel.add(usernameField);
        inputPanel.add(new JLabel("Password:"));
        inputPanel.add(passwordField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 30));
        buttonPanel.add(loginButton);

        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));

        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.PAGE_END);

        loginDialog.add(mainPanel);

        loginDialog.getRootPane().setDefaultButton(loginButton);
        loginDialog.pack();
        loginDialog.setLocationRelativeTo(parent);
        usernameField.requestFocusInWindow();

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (username.equals("admin") && password.equals("admin")) {
                JOptionPane.showMessageDialog(loginDialog, "Login successful.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loginDialog.dispose();
                showAdminPanel(parent);
            } else {
                JOptionPane.showMessageDialog(loginDialog, "Invalid credentials.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        loginDialog.setVisible(true);
    }

    /**
     * Zobrazí panel administrátora, kde sa dajú spravovať kandidáti a sledovať výsledky.
     * @param parent Referencia na rodičovské okno.
     */
    private static void showAdminPanel(JFrame parent) {
        JDialog adminDialog = new JDialog(parent, "Admin Panel", true);
        adminDialog.setAlwaysOnTop(true); // Ensure the admin panel is always on top
        adminDialog.setLayout(new BorderLayout());

        JPanel adminPanel = new JPanel(new BorderLayout());  // Define a new JPanel for admin controls
        adminDialog.add(adminPanel, BorderLayout.NORTH);

        JList<String> candidateList = new JList<>(candidates.toArray(new String[0]));
        JTextField newCandidateField = new JTextField();
        JButton addButton = new JButton("Add Candidate");
        JButton removeButton = new JButton("Remove Selected");
        JButton exitButton = new JButton("Exit");
        JButton viewResultsButton = new JButton("View Results");

        addButton.addActionListener(e -> {
            String newCandidate = newCandidateField.getText().trim();
            if (!newCandidate.isEmpty() && isAlpha(newCandidate) && !candidates.contains(newCandidate)) {
                candidates.add(newCandidate);
                candidateList.setListData(candidates.toArray(new String[0]));
                newCandidateField.setText("");
            } else {
                if (!isAlpha(newCandidate)) {
                    JOptionPane.showMessageDialog(adminDialog, "Candidate name must not include numbers.", "Invalid Name", JOptionPane.ERROR_MESSAGE);
                }
                if (candidates.contains(newCandidate)) {
                    JOptionPane.showMessageDialog(adminDialog, "This candidate is already added.", "Duplicate Candidate", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        removeButton.addActionListener(e -> {
            String selected = candidateList.getSelectedValue();
            if (selected != null) {
                candidates.remove(selected);
                candidateList.setListData(candidates.toArray(new String[0]));
            }
        });

        exitButton.addActionListener(e -> adminDialog.dispose());
        viewResultsButton.addActionListener(e -> {
            adminDialog.dispose();
            showLiveResults(parent); // This method will display the live results
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(newCandidateField, BorderLayout.CENTER);
        topPanel.add(addButton, BorderLayout.EAST);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(removeButton);
        bottomPanel.add(exitButton);
        bottomPanel.add(viewResultsButton);

        adminPanel.add(new JScrollPane(candidateList), BorderLayout.CENTER);
        adminPanel.add(topPanel, BorderLayout.NORTH);
        adminPanel.add(bottomPanel, BorderLayout.SOUTH);

        adminDialog.setSize(400, 300);
        adminDialog.setLocationRelativeTo(parent);
        adminDialog.setVisible(true);
    }

    /**
     * Vytvorí a zobrazí panel na výber kandidátov.
     * @return Panel pre výber kandidátov.
     */
    private static JPanel createCandidateSelectionPanel() {
        candidatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        candidatePanel.add(new JLabel("Choose candidate:"));
        updateCandidatePanel();
        return candidatePanel;
    }

    /**
     * Aktualizuje panel kandidátov na základe aktuálneho zoznamu.
     */
    private static void updateCandidatePanel() {
        candidatePanel.removeAll();
        candidatePanel.add(new JLabel("Choose candidate:"));
        candidatesGroup = new ButtonGroup(); // Reset ButtonGroup
        for (String candidate : candidates) {
            JRadioButton candidateButton = new JRadioButton(candidate);
            candidatesGroup.add(candidateButton);
            candidatePanel.add(candidateButton);
        }
        candidatePanel.revalidate();
        candidatePanel.repaint();
    }

    /**
     * Pridá kandidáta do zoznamu, ak je zadaný názov platný a kandidát ešte nie je na zozname.
     * @param candidateName Názov kandidáta na pridanie.
     */
    private static void addCandidate(String candidateName) {
        if (!candidateName.isEmpty() && !candidates.contains(candidateName)) {
            candidates.add(candidateName);
            updateCandidatePanel();
        }
    }

    /**
     * Odstráni kandidáta zo zoznamu.
     * @param candidateName Názov kandidáta na odstránenie.
     */
    private static void removeCandidate(String candidateName) {
        candidates.remove(candidateName);
        updateCandidatePanel();
    }

    /**
     * Získava meno vybraného kandidáta.
     * @return Meno vybraného kandidáta, alebo prázdny reťazec, ak nie je žiaden vybraný.
     */
    private static String getSelectedCandidate() {
        for (Enumeration<AbstractButton> buttons = candidatesGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                return button.getText();
            }
        }
        return ""; // Vráti prázdny reťazec, ak nie je žiaden kandidát vybraný
    }



    //----------------------------------------------------------------------------------------------------------------
    
    /**
     * Vytvorí a zobrazí úvodný "splash screen", ktorý sa automaticky zatvorí po 2 sekundách a zobrazí hlavné GUI.
     * Tento splash screen slúži ako vizuálne predstavenie aplikácie pri spustení.
     */

    private static void createSplashScreen() {
        JWindow splashScreen = new JWindow();
        splashScreen.setSize(600, 400);
        splashScreen.setLocationRelativeTo(null);
    
        // Hlavný panel, ktorý použijeme pre celý obsah splash screen
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10)); // Priamy BorderLayout s malou medzerou medzi komponentmi
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Okraj okolo celého panelu
    
        // Nadpis
        JLabel splashTitle = new JLabel("2024 election", SwingConstants.CENTER);
        splashTitle.setFont(new Font("Serif", Font.BOLD, 24)); // Nastavíme font a veľkosť podľa potreby
    
        // Obrázok
        JLabel imageLabel = new JLabel(new ImageIcon("C:\\Users\\filip\\Desktop\\Skola\\2.semester\\OOP\\projekt_Hromada_Filip\\obrazok.png"));
        
        // Pridanie komponentov do hlavného panelu
        contentPanel.add(splashTitle, BorderLayout.NORTH); // Nadpis na vrchu
        contentPanel.add(imageLabel, BorderLayout.CENTER); // Obrázok v strede
    
        // Pridanie hlavného panelu do JWindow
        splashScreen.getContentPane().add(contentPanel);
        splashScreen.setVisible(true);
    
        // Timer pre automatické zatvorenie splash screen a otvorenie hlavného GUI
        Timer splashTimer = new Timer(2000, e -> {
            splashScreen.dispose(); // Zatvorí splash screen
            SwingUtilities.invokeLater(() -> createAndShowGUI()); // Zavolá metódu na zobrazenie hlavného GUI
        });
        splashTimer.setRepeats(false); // Uistíme sa, že sa timer spustí len raz
        splashTimer.start(); // Spustenie časovača
    }
    
    /**
     * Vytvorí úvodný panel aplikácie, ktorý zobrazuje privítací text a obrázok.
     * Tento panel slúži ako prvý kontakt s používateľom.
     * @return Panel s privítacím textom a obrázkom.
     */

    private static JPanel createIntroPanel() {
        JPanel introPanel = new JPanel(new BorderLayout(10, 10)); // Použitie BorderLayoutu s medzerami
        introPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Okraj okolo celého panelu pre estetiku
        
        // Nadpis
        JLabel welcomeLabel = new JLabel("Welcome to the voting app.", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Serif", Font.BOLD, 24)); // Nastavíme font a veľkosť
        
        // Obrázok
        JLabel imageLabel = new JLabel(new ImageIcon("C:\\Users\\filip\\Desktop\\Skola\\2.semester\\OOP\\projekt_Hromada_Filip\\obrazok.png"));
        
        // Pridanie komponentov do panelu
        introPanel.add(welcomeLabel, BorderLayout.NORTH); // Nadpis na vrchu
        introPanel.add(imageLabel, BorderLayout.CENTER); // Obrázok v strede
    
        return introPanel;
    }
    
    /**
     * Vytvorí panel s tlačidlami pre hlavné rozhranie aplikácie.
     * Každé tlačidlo má priradenú špecifickú funkcionalitu, ako sú rôzne spôsoby hlasovania a administratívne možnosti.
     * @param frame Hlavné okno, do ktorého sa panel pridá.
     * @return Panel obsahujúci tlačidlá.
     */

    private static JPanel createButtonPanel(JFrame frame) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton btnOnlineVoting = new JButton("Online voting");
        JButton btnPostalVoting = new JButton("Postal voting");
        JButton btnAssistanceVoting = new JButton("Assistance voting");
        JButton adminButton = new JButton("Admin Login");
        JButton btnEnd = new JButton("End voting");

        btnOnlineVoting.addActionListener(e -> showOnlineVotingDialog(frame));
        btnPostalVoting.addActionListener(e -> showPostalVotingForm(frame));
        btnAssistanceVoting.addActionListener(e -> showAssistanceRequestForm(frame));
        adminButton.addActionListener(e -> showAdminLogin(frame));
        btnEnd.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, VotingManager.getFormattedResults(), "Results:", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        });

        buttonPanel.add(btnOnlineVoting);
        buttonPanel.add(btnPostalVoting);
        buttonPanel.add(btnAssistanceVoting);
        buttonPanel.add(adminButton);
        buttonPanel.add(btnEnd);

        return buttonPanel;
    }

    /**
     * Nastaví vzhľad aplikácie na "Nimbus" alebo na predvolený vzhľad, ak nastavenie Nimbusu zlyhá.
     */

    private static void setLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Handle potential exceptions - could log or print error message
        }
    }

    /**
     * Vytvorí panel s komponentom a popiskom zarovnanými naľavo.
     * Táto metóda je užitočná pri vytváraní rozhraní s konzistentným usporiadaním komponentov.
     * @param label Popisok komponentu.
     * @param component Samotný komponent.
     * @return Panel obsahujúci label a komponent.
     */

    private static JPanel createLeftAlignedPanel(JLabel label, JComponent component) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(label);
        panel.add(component);
        return panel;
    }

    //online hlasovnanie---------------------------------------------------------------------------------------------------------------------

    /**
     * Zobrazuje dialógové okno pre online hlasovanie. Umožňuje užívateľovi zadať osobné údaje a vybrať kandidáta,
     * po ktorom nasleduje overenie a uloženie hlasu.
     * V prípade úspešného hlasovania sa zobrazí potvrdzujúce okno, v prípade chyby sa zobrazí chybové hlásenie.
     * 
     * @param parent Rodičovské okno, na ktoré sa dialóg vzťahuje.
     */

    private static void showOnlineVotingDialog(JFrame parent) {
        JDialog dialog = new JDialog(parent, "Online Voting", true);
        dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
    
        dialog.add(createLeftAlignedPanel(new JLabel("Name and surname:"), txtName));
        dialog.add(createLeftAlignedPanel(new JLabel("Age:"), txtAge));
        dialog.add(createLeftAlignedPanel(new JLabel("ID:"), txtID));
        dialog.add(createLeftAlignedPanel(new JLabel("Zip code:"), txtZipCode));
        JComboBox<String> stateComboBox = new JComboBox<>(getAllCountries());
        dialog.add(createLeftAlignedPanel(new JLabel("State:"), stateComboBox));
        
        txtName.setText("");
        txtAge.setText("");
        txtID.setText("");
        txtZipCode.setText("");

        // Candidate selection setup
        JPanel candidatePanel = createCandidateSelectionPanel();
        dialog.add(candidatePanel);
        
    

    
        JButton submitButton = new JButton("Submit Vote");
        submitButton.addActionListener(e -> {
            String voterName = txtName.getText().trim();
            String id = txtID.getText().trim();
            String zipCode = txtZipCode.getText().trim();
            String selectedState = (String) stateComboBox.getSelectedItem();
            String selectedCandidate = getSelectedCandidate(); // Make sure to set this client property when creating the panel
            int age;
    
            try {
                age = Integer.parseInt(txtAge.getText().trim());
                if (age < 18 || age > 120) {
                    JOptionPane.showMessageDialog(dialog, "Please enter valid age.", "Invalid Age", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid age.", "Invalid Age", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!isAlpha(voterName)) {
                JOptionPane.showMessageDialog(dialog, "Name must contain only letters and spaces.", "Invalid Name", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!isNumeric(zipCode)) {
                JOptionPane.showMessageDialog(dialog, "Zip code must be numeric.", "Invalid Zip Code", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            if (voterName.isEmpty() || id.isEmpty() || zipCode.isEmpty() || selectedCandidate.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill out all fields.", "Incomplete Form", JOptionPane.ERROR_MESSAGE);
                return;
            }


    
            // Assuming VotingManager.addVote() processes the vote
            if (VotingManager.addVote(selectedCandidate, voterName, age, selectedState, id, Integer.parseInt(zipCode))) {
                JOptionPane.showMessageDialog(dialog, "Your vote has been successfully cast for: " + selectedCandidate, "Vote Submitted", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "You have already voted or there was an error.", "Voting Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    
        dialog.add(submitButton);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
    






    //postove hlasovanie---------------------------------------------------------------------------------------------------------------------

    /**
     * Zobrazuje dialógové okno pre žiadosť o hlasovanie poštou. Umožňuje užívateľovi požiadať o hlasovací formulár a zadať osobné údaje,
     * po ktorom nasleduje samotné hlasovanie.
     * V prípade úspešného hlasovania sa zobrazí potvrdzujúce okno, v prípade chyby sa zobrazí chybové hlásenie.
     * 
     * @param parent Rodičovské okno, na ktoré sa dialóg vzťahuje.
     */

    private static void showPostalVotingForm(JFrame parent) {
        JDialog formDialog = new JDialog(parent, "Request for Postal Voting Form", true);
        formDialog.setLayout(new BoxLayout(formDialog.getContentPane(), BoxLayout.Y_AXIS));
    
        // Local variables for form
        JTextField localTxtName = new JTextField(15);
        JTextField localTxtID = new JTextField(15);
        JTextField localTxtAddress = new JTextField(15);
        JTextField localTxtZipCode = new JTextField(15);
        JComboBox<String> localStateComboBox = new JComboBox<>(getAllCountries()); // Ensure this method works correctly
    
        // Add components to form
        formDialog.add(createLeftAlignedPanel(new JLabel("Name and surname:"), localTxtName));
        formDialog.add(createLeftAlignedPanel(new JLabel("ID:"), localTxtID));
        formDialog.add(createLeftAlignedPanel(new JLabel("Address:"), localTxtAddress));
        formDialog.add(createLeftAlignedPanel(new JLabel("Zip code:"), localTxtZipCode));
        formDialog.add(createLeftAlignedPanel(new JLabel("State:"), localStateComboBox));
    
        JButton submitFormButton = new JButton("Request Form");
        submitFormButton.addActionListener(e -> {
            String name = localTxtName.getText().trim();
            String id = localTxtID.getText().trim();
            String address = localTxtAddress.getText().trim();
            String zipCode = localTxtZipCode.getText().trim();
            String state = (String) localStateComboBox.getSelectedItem();

            if (VotingManager.hasAlreadyVoted(id)) {
                JOptionPane.showMessageDialog(formDialog, "This ID has already been used to vote.", "Already Voted", JOptionPane.ERROR_MESSAGE);
                formDialog.dispose();
                return;
            }

            if (!isAlpha(name)) {
                JOptionPane.showMessageDialog(formDialog, "Name must contain only letters and spaces.", "Invalid Name", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!isNumeric(zipCode)) {
                JOptionPane.showMessageDialog(formDialog, "Zip code must be numeric.", "Invalid Zip Code", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (name.isEmpty() || id.isEmpty() || address.isEmpty() || zipCode.isEmpty() || state.isEmpty()) {
                JOptionPane.showMessageDialog(formDialog, "All fields must be filled out", "Incomplete Form", JOptionPane.ERROR_MESSAGE);
            } else {
                // Inform the user
                JOptionPane.showMessageDialog(formDialog, "Your postal voting form request has been submitted.", "Form Requested", JOptionPane.INFORMATION_MESSAGE);
                formDialog.dispose();
                SwingUtilities.invokeLater(() -> showPostalVotingSimulation(parent, name, id, zipCode, state));
            }
        });
    
        formDialog.add(submitFormButton);
        formDialog.pack();
        formDialog.setLocationRelativeTo(parent);
        formDialog.setVisible(true);
    }
    
    
    
    /**
     * Zobrazuje dialógové okno pre hlasovanie poštou. Umožňuje užívateľovi zadať osobné údaje,
     * po ktorom nasleduje samotné hlasovanie.
     * V prípade úspešného hlasovania sa zobrazí potvrdzujúce okno, v prípade chyby sa zobrazí chybové hlásenie.
     * 
     * @param parent Rodičovské okno, na ktoré sa dialóg vzťahuje.
     * @param name Meno a priezvisko voliča.
     * @param originalID Pôvodné ID voliča.
     * @param zipCode PSČ voliča.
     * @param state Štát voliča.
     */

    private static void showPostalVotingSimulation(JFrame parent, String name, String originalID, String zipCode, String state) {
        JDialog votingDialog = new JDialog(parent, "Postal Voting - Simulation", true);
        votingDialog.setLayout(new BoxLayout(votingDialog.getContentPane(), BoxLayout.Y_AXIS));
    
        // Confirming information fields
        JTextField txtConfirmName = new JTextField(name, 15);
        txtConfirmName.setEditable(false);  // User should not change the name here
        
        JTextField txtConfirmID = new JTextField(15);  // User will re-enter ID for confirmation
        JTextField txtConfirmAge = new JTextField(15);  // New field for entering age
        
        JComboBox<String> confirmStateComboBox = new JComboBox<>(getAllCountries());
        confirmStateComboBox.setSelectedItem(state);
        confirmStateComboBox.setEnabled(false);  // State should not change here
        
        JTextField txtConfirmZipCode = new JTextField(zipCode, 15);
        txtConfirmZipCode.setEditable(false);  // Zip code should not change here
    
        // Adding components to the dialog
        votingDialog.add(createLeftAlignedPanel(new JLabel("Confirmed Name:"), txtConfirmName));
        votingDialog.add(createLeftAlignedPanel(new JLabel("Confirm ID:"), txtConfirmID));
        votingDialog.add(createLeftAlignedPanel(new JLabel("Enter Age:"), txtConfirmAge));
        votingDialog.add(createLeftAlignedPanel(new JLabel("State:"), confirmStateComboBox));
        votingDialog.add(createLeftAlignedPanel(new JLabel("Zip Code:"), txtConfirmZipCode));
    
        // Candidate selection setup
        JPanel candidatePanel = createCandidateSelectionPanel();
        votingDialog.add(candidatePanel);
    
        // Submit vote button setup
        JButton voteButton = new JButton("Submit Vote");
        voteButton.addActionListener(e -> {
            String confirmedID = txtConfirmID.getText().trim();
            String confirmedAge = txtConfirmAge.getText().trim();
            String selectedCandidate = getSelectedCandidate(); // Make sure to set this client property when creating the panel
            int age;
    
            if (confirmedID.isEmpty() || selectedCandidate == null || confirmedAge.isEmpty()) {
                JOptionPane.showMessageDialog(votingDialog, "Please fill in all fields.", "Incomplete Submission", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            if (!confirmedID.equals(originalID)) {
                JOptionPane.showMessageDialog(votingDialog, "The ID does not match the ID entered initially.", "ID Mismatch", JOptionPane.ERROR_MESSAGE);
                return;
            }


            try {
                age = Integer.parseInt(confirmedAge);
                if (age < 18) {
                    JOptionPane.showMessageDialog(votingDialog, "You must be at least 18 years old to vote.", "Underage", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(votingDialog, "Invalid age entered.", "Invalid Age", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            // Assuming VotingManager.addVote() correctly processes the vote
            if (VotingManager.addVote(selectedCandidate, name, age, state, confirmedID, Integer.parseInt(zipCode))) {
                JOptionPane.showMessageDialog(votingDialog, "Your vote has been successfully cast for: " + selectedCandidate, "Vote Submitted", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(votingDialog, "You have already voted or there was an error.", "Voting Error", JOptionPane.ERROR_MESSAGE);
            }
            votingDialog.dispose();  // Close the dialog when done
        });
    
        votingDialog.add(voteButton);
        votingDialog.pack();
        votingDialog.setLocationRelativeTo(parent);
        votingDialog.setVisible(true);
    }
    
    

    //hlasovanie s aistenciou---------------------------------------------------------------------------------------------------------------------

    /**
     * Zobrazuje dialógové okno pre žiadosť o hlasovanie s asistenciou. Umožňuje užívateľovi požiadať o hlasovanie s asistenciou a zadať osobné údaje,
     * po ktorom nasleduje samotné hlasovanie.
     * V prípade úspešného hlasovania sa zobrazí potvrdzujúce okno, v prípade chyby sa zobrazí chybové hlásenie.
     * 
     * @param parent Rodičovské okno, na ktoré sa dialóg vzťahuje.
     */

    private static void showAssistanceRequestForm(JFrame parent) {
        JDialog assistanceDialog = new JDialog(parent, "Request for Assistance", true);
        assistanceDialog.setLayout(new BoxLayout(assistanceDialog.getContentPane(), BoxLayout.Y_AXIS));
        assistanceDialog.setSize(300, 200);

        txtName.setText("");
        txtAddress.setText("");
        txtID.setText("");
        txtZipCode.setText("");
    
        // Use class-wide text fields
        assistanceDialog.add(createLeftAlignedPanel(new JLabel("Name and surname:"), txtName));
        assistanceDialog.add(createLeftAlignedPanel(new JLabel("Address:"), txtAddress));
        assistanceDialog.add(createLeftAlignedPanel(new JLabel("ID:"), txtID));
        assistanceDialog.add(createLeftAlignedPanel(new JLabel("Zip code:"), txtZipCode));
        JComboBox<String> localStateComboBox = new JComboBox<>(getAllCountries());
        assistanceDialog.add(createLeftAlignedPanel(new JLabel("State:"), localStateComboBox));
    
        JButton btnRequestAssistance = new JButton("Request Assistance");
        btnRequestAssistance.addActionListener(e -> {
            String name = txtName.getText().trim();
            String address = txtAddress.getText().trim();
            String id = txtID.getText().trim();
            String zip = txtZipCode.getText().trim();
            String state = (String) localStateComboBox.getSelectedItem();

            if (VotingManager.hasAlreadyVoted(id)) {
                JOptionPane.showMessageDialog(assistanceDialog, "This ID has already been used to vote.", "Already Voted", JOptionPane.ERROR_MESSAGE);
                assistanceDialog.dispose();
                return;
            }

            if (!isAlpha(name)) {
                JOptionPane.showMessageDialog(assistanceDialog, "Name must contain only letters and spaces.", "Invalid Name", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!isNumeric(zip)) {
                JOptionPane.showMessageDialog(assistanceDialog, "Zip code must be numeric.", "Invalid Zip Code", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
    
            if (name.isEmpty() || address.isEmpty() || id.isEmpty() || zip.isEmpty() || state.isEmpty()) {
                JOptionPane.showMessageDialog(assistanceDialog, "Please fill out all fields.", "Incomplete Form", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(assistanceDialog, "Your request for assistance has been submitted.", "Request Submitted", JOptionPane.INFORMATION_MESSAGE);
                assistanceDialog.dispose();
                showAssistanceTimeWindow(parent, name, address, id, zip, state);
            }
        });
        
        // Create a panel for buttons to make the layout consistent
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(btnRequestAssistance);
    
        assistanceDialog.add(buttonPanel); // Add the button panel to the dialog
    
        assistanceDialog.pack();
        assistanceDialog.setLocationRelativeTo(parent);
        assistanceDialog.setVisible(true);
    }
    
    /**
     * Zobrazuje dialógové okno s časom kedz príde asistent
     * 
     * @param parent Rodičovské okno, na ktoré sa dialóg vzťahuje.
     * @param name Meno a priezvisko voliča.
     * @param address Adresa voliča.
     * @param id ID voliča.
     * @param zip PSČ voliča.
     * @param state Štát voliča.
     */

    private static void showAssistanceTimeWindow(JFrame parent, String name, String address, String id, String zip, String state) {
        Random rand = new Random();
        int appointmentHour = 9 + rand.nextInt(8); // Assistants are available from 9 AM to 4 PM
        JOptionPane.showMessageDialog(parent,
            String.format("An assistant will arrive at your location between %02d:00 and %02d:00.", appointmentHour, appointmentHour + 1),
            "Assistant Arrival Time", JOptionPane.INFORMATION_MESSAGE);
    
        showAssistanceVoting(parent, name, id, zip, state);
    }


    /**
     * Zobrazuje dialógové okno pre hlasovanie s asistenciou. Umožňuje užívateľovi zadať osobné údaje,
     * po ktorom nasleduje samotné hlasovanie.
     * V prípade úspešného hlasovania sa zobrazí potvrdzujúce okno, v prípade chyby sa zobrazí chybové hlásenie.
     * 
     * @param parent Rodičovské okno, na ktoré sa dialóg vzťahuje.
     * @param name Meno a priezvisko voliča.
     * @param id ID voliča.
     * @param zip PSČ voliča.
     * @param state Štát voliča.
     */

    private static void showAssistanceVoting(JFrame parent, String name, String id, String zip, String state) {
        JDialog votingDialog = new JDialog(parent, "Assistance Voting", true);
        votingDialog.setLayout(new BoxLayout(votingDialog.getContentPane(), BoxLayout.Y_AXIS));

        // Use JLabel to display name
        JLabel lblName = new JLabel(name);
        lblName.setPreferredSize(new Dimension(200, 20)); // Set the preferred size (width x height)

        // Initialize txtConfirmID for ID confirmation, editable to allow user input
        JTextField txtConfirmID = new JTextField(15); // Optionally pre-filled with the ID
        txtConfirmID.setEditable(true);
    // Set the preferred size

        // Initialize txtConfirmAge for age confirmation, editable to allow user input
        JTextField txtConfirmAge = new JTextField(15); // Leave empty or pre-fill as required
        txtConfirmAge.setEditable(true);
    // Set the preferred size

        // Use the createLeftAlignedPanel method for adding components in an aligned manner
        votingDialog.add(createLeftAlignedPanel(new JLabel("Name:"), lblName));
        votingDialog.add(createLeftAlignedPanel(new JLabel("Confirm ID:"), txtConfirmID));
        votingDialog.add(createLeftAlignedPanel(new JLabel("Confirm Age:"), txtConfirmAge));

        // Candidate selection setup
        JPanel candidatePanel = createCandidateSelectionPanel();
        votingDialog.add(candidatePanel);

        JButton btnVote = new JButton("Submit Vote");
        btnVote.addActionListener(e -> {
            String confirmedID = txtConfirmID.getText().trim();
            String confirmedAge = txtConfirmAge.getText().trim();
            if (!confirmedID.equals(id)) {
                JOptionPane.showMessageDialog(votingDialog, "The ID does not match the initial request.", "ID Mismatch", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String selectedCandidate = getSelectedCandidate();
            int age;
            try {
                age = Integer.parseInt(confirmedAge);
                if (age >= 18) { // Corrected from 'age > 18' to 'age >= 18'
                    // Assuming VotingManager.addVote() correctly processes the vote
                    if (VotingManager.addVote(selectedCandidate, name, age, state, confirmedID, Integer.parseInt(zip))) {
                        JOptionPane.showMessageDialog(votingDialog, "Your vote has been successfully cast for: " + selectedCandidate, "Vote Submitted", JOptionPane.INFORMATION_MESSAGE);
                        votingDialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(votingDialog, "An error occurred during voting.", "Vote Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(votingDialog, "You must be 18 years or older to vote.", "Age Requirement Not Met", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(votingDialog, "Please enter a valid age.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        });
        
            votingDialog.add(btnVote);
            votingDialog.pack();
            votingDialog.setLocationRelativeTo(parent);
            votingDialog.setVisible(true);
        }
        
    }