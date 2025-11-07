package com.library;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class LibraryGUI extends JFrame {
    private Inventory inventory = new Inventory();
    private java.util.List<Loan> loans = new ArrayList<>();
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> displayList = new JList<>(listModel);

    public LibraryGUI() {
        setTitle("Biblioteca - ETITC");
        setSize(700,400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initData();
        initUI();
        refreshList();
    }

    private void initData() {
        inventory.addMaterial(new Book("B001","Programación en Java","José Perez",2018));
        inventory.addMaterial(new Book("B002","Estructuras de Datos","Ana Gómez",2016));
        inventory.addMaterial(new Book("B003","Bases de Datos","Luis Martínez",2019));
    }

    private void initUI() {
        JPanel left = new JPanel(new BorderLayout());
        left.setBorder(BorderFactory.createTitledBorder("Inventario / Estado"));
        left.add(new JScrollPane(displayList), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(5,1,5,5));
        JButton btnInventory = new JButton("Ver Inventario");
        JButton btnLoan = new JButton("Registrar Préstamo");
        JButton btnReturn = new JButton("Registrar Devolución");
        JButton btnFines = new JButton("Calcular Multa");
        JButton btnExit = new JButton("Salir");

        buttons.add(btnInventory);
        buttons.add(btnLoan);
        buttons.add(btnReturn);
        buttons.add(btnFines);
        buttons.add(btnExit);

        add(left, BorderLayout.CENTER);
        add(buttons, BorderLayout.EAST);

        btnInventory.addActionListener(e -> refreshList());
        btnLoan.addActionListener(e -> handleLoan());
        btnReturn.addActionListener(e -> handleReturn());
        btnFines.addActionListener(e -> handleFines());
        btnExit.addActionListener(e -> System.exit(0));
    }

    private void refreshList() {
        listModel.clear();
        for (Material m : inventory.listAll()) listModel.addElement(m.toString());
    }

    private void handleLoan() {
        java.util.List<Material> avail = inventory.listAvailable();
        if (avail.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay material disponible.");
            return;
        }
        String[] options = avail.stream().map(m->m.getId()+" - "+m.getTitle()).toArray(String[]::new);
        String choice = (String) JOptionPane.showInputDialog(this, "Seleccione material:",
                "Préstamo", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (choice==null) return;
        String id = choice.split(" - ")[0];
        Material mat = inventory.getMaterial(id);
        String borrower = JOptionPane.showInputDialog(this, "Nombre del prestatario:");
        if (borrower==null || borrower.trim().isEmpty()) return;
        int days = 14;
        try { days = Integer.parseInt(JOptionPane.showInputDialog(this, "Días de préstamo:", "14")); } catch(Exception e) {}
        loans.add(new Loan(mat, borrower, LocalDate.now(), days));
        JOptionPane.showMessageDialog(this, "Préstamo registrado.");
        refreshList();
    }

    private void handleReturn() {
        java.util.List<Loan> active = new ArrayList<>();
        for (Loan l : loans) if (l.getReturnDate() == null) active.add(l);
        if (active.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay préstamos activos.");
            return;
        }
        String[] options = active.stream().map(l->l.getMaterial().getId()+" - "+l.getBorrower()+" (vence: "+l.getDueDate()+")").toArray(String[]::new);
        String choice = (String) JOptionPane.showInputDialog(this, "Seleccione préstamo:", "Devolución",
                JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (choice==null) return;
        Loan sel = active.get(Arrays.asList(options).indexOf(choice));

        LocalDate returnDate = askForDate("Ingrese la fecha de devolución (AAAA-MM-DD):");
        if (returnDate == null) return;

        sel.setReturnDate(returnDate);
        JOptionPane.showMessageDialog(this, "Devolución registrada.Días de retraso: " + sel.daysLate(returnDate));
        refreshList();
    }

    private void handleFines() {
        double rate = 1.0;
        try { rate = Double.parseDouble(JOptionPane.showInputDialog(this, "Tarifa por día:", "1.0")); } catch(Exception e) {}

        LocalDate refDate = askForDate("Ingrese la fecha de referencia (AAAA-MM-DD):");
        if (refDate == null) refDate = LocalDate.now();

        StringBuilder sb = new StringBuilder();
        for (Loan l : loans) {
            long daysLate = l.daysLate(refDate);
            if (daysLate > 0) {
                sb.append(String.format("%s | %s | %s - Días: %d - Multa: %.2f (%s)%n",
                    l.getMaterial().getId(), l.getMaterial().getTitle(), l.getBorrower(),
                    daysLate, l.fineAmount(rate, refDate), (l.getReturnDate()==null?"Activo":"Devuelto")));
            }
        }
        if (sb.length()==0) sb.append("No hay multas registradas a la fecha indicada.");
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Multas", JOptionPane.INFORMATION_MESSAGE);
    }

    private LocalDate askForDate(String message) {
        while (true) {
            String input = JOptionPane.showInputDialog(this, message);
            if (input == null) return null;
            try {
                return LocalDate.parse(input.trim());
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Formato incorrecto. Use AAAA-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}