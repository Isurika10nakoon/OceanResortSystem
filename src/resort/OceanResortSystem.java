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

    // ── Logged-in user info (set after successful login) ──────────
    private String loggedInRole     = "";
    private String loggedInUsername = "";
    private String loggedInFullName = "";

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
        loadReservationsFromDB();
        new LoginPage(this);
    }

    /* ===================== GETTERS / SETTERS ===================== */
    public HashMap<String, Reservation> getReservations() { return reservations; }

    public String getLoggedInRole()     { return loggedInRole; }
    public String getLoggedInUsername() { return loggedInUsername; }
    public String getLoggedInFullName() { return loggedInFullName; }

    /** Called by LoginPage after a successful login to store who is logged in */
    public void setLoggedInUser(String username, String fullName, String role) {
        this.loggedInUsername = username;
        this.loggedInFullName = fullName;
        this.loggedInRole     = role;
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

    /* ===================== DATABASE: LOAD RESERVATIONS ===================== */
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
            System.out.println("Loaded " + reservations.size() + " reservations from DB.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Error loading reservations:\n" + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* ===================== DATABASE: INSERT RESERVATION ===================== */
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
            reservations.put(r.resNo, r);
            System.out.println("Reservation " + r.resNo + " inserted.");
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Error saving reservation:\n" + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /* ===================== DATABASE: DELETE RESERVATION ===================== */
    public boolean deleteReservation(String resNo) {
        String sql = "DELETE FROM reservations WHERE res_no = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, resNo);
            ps.executeUpdate();
            reservations.remove(resNo);
            System.out.println("Reservation " + resNo + " deleted.");
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Error deleting reservation:\n" + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /* ===================== DATABASE: VALIDATE LOGIN ===================== */
    /**
     * Queries the staff table for matching username + password.
     * If valid: stores logged-in user info AND returns the role ("Admin" or "Staff").
     * If invalid: returns null.
     */
    public String validateStaffLogin(String username, String password) {
        String sql = "SELECT full_name, role FROM staff WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String fullName = rs.getString("full_name");
                String role     = rs.getString("role");
                setLoggedInUser(username, fullName, role);  // store for MainMenu
                return role;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /* ===================== DATABASE: STAFF COUNT ===================== */
    public int getStaffCount() {
        String sql = "SELECT COUNT(*) FROM staff";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /* ===================== DATABASE: UPDATE RESERVATION ===================== */
    public boolean updateReservation(String resNo, String name, String address,
            String contact, String roomType, LocalDate checkIn, LocalDate checkOut) {
        String sql = "UPDATE reservations SET guest_name=?, address=?, contact=?, "
                   + "room_type=?, check_in=?, check_out=? WHERE res_no=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, address);
            ps.setString(3, contact);
            ps.setString(4, roomType);
            ps.setDate(5, java.sql.Date.valueOf(checkIn));
            ps.setDate(6, java.sql.Date.valueOf(checkOut));
            ps.setString(7, resNo);
            ps.executeUpdate();

            // Update in-memory cache too
            Reservation r = reservations.get(resNo);
            if (r != null) {
                r.name     = name;
                r.address  = address;
                r.contact  = contact;
                r.roomType = roomType;
                r.checkIn  = checkIn;
                r.checkOut = checkOut;
                r.updateStatus();
            }
            System.out.println("Reservation " + resNo + " updated.");
            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Error updating reservation:\n" + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== COMPATIBILITY ===================== */
    void saveReservationsToFile() { /* replaced by DB */ }

    /* ===================== MAIN ===================== */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { e.printStackTrace(); }
        SwingUtilities.invokeLater(() -> new OceanResortSystem());
    }
}
