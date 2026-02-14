package resort;

import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.util.*;
import javax.swing.*;

public class OceanResortSystem extends JFrame {

    /* ===================== DATA MODEL ===================== */
    static class Reservation {
        String resNo, name, address, contact, roomType;
        LocalDate checkIn, checkOut;
        String status;

        Reservation(String resNo, String name, String address, String contact,
                    String roomType, LocalDate checkIn, LocalDate checkOut) {
            this.resNo = resNo;
            this.name = name;
            this.address = address;
            this.contact = contact;
            this.roomType = roomType;
            this.checkIn = checkIn;
            this.checkOut = checkOut;
            updateStatus();
        }
        
        void updateStatus() {
            LocalDate today = LocalDate.now();
            if (checkOut.isBefore(today)) {
                status = "Completed";
            } else if (checkIn.isAfter(today)) {
                status = "Upcoming";
            } else if (!checkIn.isAfter(today) && !checkOut.isBefore(today)) {
                status = "Active";
            } else {
                status = "Upcoming";
            }
        }
    }

    private HashMap<String, Reservation> reservations = new HashMap<>();
    private static final String FILE_NAME = "reservations.txt";

    /* ===================== MODERN THEME ===================== */
    final Color PRIMARY_COLOR = new Color(25, 118, 210);
    final Color ACCENT_COLOR = new Color(66, 165, 245);
    final Color SUCCESS_COLOR = new Color(76, 175, 80);
    final Color WARNING_COLOR = new Color(255, 152, 0);
    final Color DANGER_COLOR = new Color(244, 67, 54);
    final Color INFO_COLOR = new Color(41, 182, 246);
    final Color SIDEBAR_COLOR = new Color(38, 50, 56);
    final Color SIDEBAR_HOVER = new Color(55, 71, 79);
    final Color BG_COLOR = new Color(250, 250, 250);
    final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public OceanResortSystem() {
        loadReservationsFromFile();
        new LoginPage(this);
    }

    public HashMap<String, Reservation> getReservations() {
        return reservations;
    }

    public void showMainMenu() {
        new MainMenu(this);
    }

    /* ===================== UTILITIES ===================== */
    int getRoomRate(String roomType) {
        switch(roomType) {
            case "Single": return 8000;
            case "Double": return 12000;
            case "Suite": return 20000;
            default: return 0;
        }
    }

    /* ===================== FILE HANDLING ===================== */
    void saveReservationsToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Reservation r : reservations.values()) {
                bw.write(r.resNo + "," + r.name + "," + r.address + "," + r.contact + "," + 
                         r.roomType + "," + r.checkIn + "," + r.checkOut);
                bw.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving data!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadReservationsFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 7) {
                    Reservation r = new Reservation(
                        data[0], data[1], data[2], data[3], data[4],
                        LocalDate.parse(data[5]), LocalDate.parse(data[6])
                    );
                    reservations.put(r.resNo, r);
                }
            }
        } catch (IOException e) {
            // File doesn't exist on first run
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new OceanResortSystem());
    }
}
