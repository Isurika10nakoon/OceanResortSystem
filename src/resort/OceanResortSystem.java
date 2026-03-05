package resort;

import java.awt.*;
import java.sql.*;
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
            this.resNo    = resNo;
            this.name     = name;
            this.address  = address;
            this.contact  = contact;
            this.roomType = roomType;
            this.checkIn  = checkIn;
            this.checkOut = checkOut;
            updateStatus();
        }

        void updateStatus() {
            LocalDate today = LocalDate.now();
            if (checkOut.isBefore(today)) {
                status = "Completed";
            } else if (checkIn.isAfter(today)) {
                status = "Upcoming";
            } else {
                status = "Active";
            }
        }
    }

    // In-memory cache of reservations loaded from DB
    private HashMap<String, Reservation> reservations = new HashMap<>();

    /* ===================== MODERN THEME ===================== */
    final Color PRIMARY_COLOR  = new Color(25, 118, 210);
    final Color ACCENT_COLOR   = new Color(66, 165, 245);
    final Color SUCCESS_COLOR  = new Color(76, 175, 80);
    final Color WARNING_COLOR  = new Color(255, 152, 0);
    final Color DANGER_COLOR   = new Color(244, 67, 54);
    final Color INFO_COLOR     = new Color(41, 182, 246);
    final Color SIDEBAR_COLOR  = new Color(38, 50, 56);
    final Color SIDEBAR_HOVER  = new Color(55, 71, 79);
    final Color BG_COLOR       = new Color(250, 250, 250);
    final Font  MAIN_FONT      = new Font("Segoe UI", Font.PLAIN, 14);

    /* ===================== CONSTRUCTOR ===================== */
    public OceanResortSystem() {
        loadReservationsFromDB();   // load from MySQL on startup
        new LoginPage(this);
    }

    /* ===================== GETTERS ===================== */
    public HashMap<String, Reservation> getReservations() {
        return reservations;
    }

    public void showMainMenu() {
        new MainMenu(this);
    }

    /* ===================== ROOM RATES ===================== */
    int getRoomRate(String roomType) {
        switch (roomType) {
            case "Single": return 8000;
            case "Double": return 12000;
            case "Suite":  return 20000;
            default:       return 0;
        }
    }

    /* ===================== DATABASE: LOAD ALL ===================== */
    /**
     * Reads every row from the reservations table into the in-memory HashMap.
     * Called once at startup and after any write operation to stay in sync.
     */
    public void loadReservationsFromDB() {
        reservations.clear();
        String sql = "SELECT * FROM reservations";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Reservation r = new Reservation(
                    rs.getString("res_no"),
                    rs.getString("guest_name"),
                    rs.getString("address"),
                    rs.getString("contact"),
                    rs.getString("room_type"),
                    rs.getDate("check_in").toLocalDate(),
                    rs.getDate("check_out").toLocalDate()
                );
                reservations.put(r.resNo, r);
            }
            System.out.println("✅ Loaded " + reservations.size() + " reservations from database.");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Error loading reservations from database:\n" + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /* ===================== DATABASE: INSERT ===================== */
    /**
     * Inserts a new reservation row into MySQL.
     * Called from AddReservation.java when staff clicks "Save Reservation".
     */
    public boolean insertReservation(Reservation r) {
        String sql = "INSERT INTO reservations "
                   + "(res_no, guest_name, address, contact, room_type, check_in, check_out) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, r.resNo);
            ps.setString(2, r.name);
            ps.setString(3, r.address);
            ps.setString(4, r.contact);
            ps.setString(5, r.roomType);
            ps.setDate(6, java.sql.Date.valueOf(r.checkIn));
            ps.setDate(7, java.sql.Date.valueOf(r.checkOut));

            ps.executeUpdate();
            reservations.put(r.resNo, r);   // keep cache in sync
            System.out.println("✅ Reservation " + r.resNo + " inserted into database.");
            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Error saving reservation to database:\n" + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== DATABASE: DELETE ===================== */
    /**
     * Deletes a reservation row by reservation number.
     * Called from ViewReservations.java when staff clicks Delete.
     */
    public boolean deleteReservation(String resNo) {
        String sql = "DELETE FROM reservations WHERE res_no = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, resNo);
            ps.executeUpdate();
            reservations.remove(resNo);     // keep cache in sync
            System.out.println("✅ Reservation " + resNo + " deleted from database.");
            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Error deleting reservation:\n" + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== KEPT FOR COMPATIBILITY ===================== */
    /**
     * Old file-save method — now delegates to insertReservation().
     * Keeping the name so other classes that call saveReservationsToFile()
     * don't break during the transition.
     */
    void saveReservationsToFile() {
        // No longer writes a file — DB is the source of truth.
        // Individual inserts are handled by insertReservation().
    }

    /* ===================== MAIN ===================== */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new OceanResortSystem());
    }
}
