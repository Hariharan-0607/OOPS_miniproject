import java.io.*;
import java.net.*;
import java.sql.*;

public class Server {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("✅ Server started. Waiting for client connection...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("📡 Client connected.");

                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                String input = in.readUTF();
                String[] data = input.split(",");

                int studentId = Integer.parseInt(data[0]);
                double exam1 = Double.parseDouble(data[1]);
                double exam2 = Double.parseDouble(data[2]);
                double exam3 = Double.parseDouble(data[3]);
                double assignment = Double.parseDouble(data[4]);
                double semester = Double.parseDouble(data[5]);

                // ✅ Internal marks calculation
                double exam1Part = (exam1 / 50.0) * 10;
                double exam2Part = (exam2 / 25.0) * 5;
                double exam3Part = (exam3 / 50.0) * 10;
                double assignmentPart = (assignment / 50.0) * 10;

                double internalTotal = exam1Part + exam2Part + exam3Part + assignmentPart; // out of 35
                double internalOutOf40 = (internalTotal / 35.0) * 40;
                double semConverted = (semester / 100.0) * 60;
                double finalTotal = internalOutOf40 + semConverted;

                // ✅ Grade calculation
                String grade;
                if (finalTotal >= 90) grade = "O";
                else if (finalTotal >= 80) grade = "A+";
                else if (finalTotal >= 70) grade = "A";
                else if (finalTotal >= 60) grade = "B+";
                else if (finalTotal >= 50) grade = "B";
                else grade = "Fail";

                // 🗄 Save to MySQL database
                saveToDatabase(studentId, internalOutOf40, finalTotal, grade);

                String result = String.format(
                    "Internal: %.2f / 40\nFinal: %.2f / 100\nGrade: %s",
                    internalOutOf40, finalTotal, grade
                );
                out.writeUTF(result);

                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveToDatabase(int id, double internal, double total, String grade) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/internaldb", "root", "harini15");
            String query = "INSERT INTO internal_marks (student_id, internal, total, grade) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, id);
            ps.setDouble(2, internal);
            ps.setDouble(3, total);
            ps.setString(4, grade);
            ps.executeUpdate();
            con.close();
        } catch (Exception e) {
            System.out.println("⚠ Database error: " + e.getMessage());
        }
    }
}
