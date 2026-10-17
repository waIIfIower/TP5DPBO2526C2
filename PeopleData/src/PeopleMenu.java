import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PeopleMenu extends JFrame {
    private JPanel mainPanel;
    private JTextField txtId;
    private JTextField txtNama;
    private JTextField txtTahunLahir;
    private JComboBox<String> cbKategori;
    private JRadioButton rbLaki;
    private JRadioButton rbPerempuan;
    private ButtonGroup bgJenisKelamin;
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnCancel;
    private JTable tblPeople;
    private JScrollPane scrollPane;

    private Database db;
    private int selectedIndex = -1;

    public PeopleMenu() {
        db = new Database();

        bgJenisKelamin = new ButtonGroup();
        bgJenisKelamin.add(rbLaki);
        bgJenisKelamin.add(rbPerempuan);

        cbKategori.setModel(new DefaultComboBoxModel<>(new String[]{"Full Time", "Part Time", "Kontrak", "Lainnya"}));

        setTable();

        btnAdd.addActionListener(e -> insertData());
        btnUpdate.addActionListener(e -> updateData());
        btnDelete.addActionListener(e -> deleteData());
        btnCancel.addActionListener(e -> clearForm());

        tblPeople.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedIndex = tblPeople.getSelectedRow();
                if (selectedIndex != -1) {
                    txtId.setText(tblPeople.getValueAt(selectedIndex, 0).toString());
                    txtNama.setText(tblPeople.getValueAt(selectedIndex, 1).toString());
                    txtTahunLahir.setText(tblPeople.getValueAt(selectedIndex, 2).toString());
                    cbKategori.setSelectedItem(tblPeople.getValueAt(selectedIndex, 3).toString());

                    String jk = tblPeople.getValueAt(selectedIndex, 4).toString();
                    if ("Laki-laki".equalsIgnoreCase(jk)) {
                        rbLaki.setSelected(true);
                    } else {
                        rbPerempuan.setSelected(true);
                    }

                    txtId.setEditable(false);
                    btnAdd.setEnabled(false);
                    btnUpdate.setEnabled(true);
                    btnDelete.setEnabled(true);
                }
            }
        });

        clearForm();
    }

    // Read Data dari tabel 'person'
    public void setTable() {
        String[] columnNames = {"ID", "Nama", "Tahun Lahir", "Kategori", "Jenis Kelamin"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        try {
            ResultSet rs = db.selectQuery("SELECT * FROM person");
            while (rs.next()) {
                Object[] rowData = {
                        rs.getString("id"),
                        rs.getString("nama"),
                        rs.getInt("tahun_lahir"),
                        rs.getString("kategori"),
                        rs.getString("jenis_kelamin")
                };
                model.addRow(rowData);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mengambil data dari database: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
        }

        tblPeople.setModel(model);
    }

    // Insert Data ke tabel 'person'
    private void insertData() {
        String id = txtId.getText().trim();
        String nama = txtNama.getText().trim();
        String strTahun = txtTahunLahir.getText().trim();
        String kategori = (String) cbKategori.getSelectedItem();
        String jenisKelamin = rbLaki.isSelected() ? "Laki-laki" : "Perempuan";

        if (id.isEmpty() || nama.isEmpty() || strTahun.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua kolom input wajib diisi!", "Error Input Kosong", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int tahunLahir = Integer.parseInt(strTahun);

            // Cek ID Duplikat di tabel 'person'
            ResultSet checkId = db.selectQuery("SELECT * FROM person WHERE id = '" + id + "'");
            if (checkId.next()) {
                JOptionPane.showMessageDialog(this, "ID '" + id + "' sudah terdaftar dalam database!", "Error ID Duplikat", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sql = String.format(
                    "INSERT INTO person (id, nama, tahun_lahir, kategori, jenis_kelamin) VALUES ('%s', '%s', %d, '%s', '%s')",
                    id, nama, tahunLahir, kategori, jenisKelamin
            );

            db.insertUpdateDeleteQuery(sql);
            setTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan ke database!", "Informasi", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Tahun lahir harus berupa angka!", "Error Format", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan data: " + ex.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Update Data di tabel 'person'
    private void updateData() {
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data pada tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = txtId.getText().trim();
        String nama = txtNama.getText().trim();
        String strTahun = txtTahunLahir.getText().trim();
        String kategori = (String) cbKategori.getSelectedItem();
        String jenisKelamin = rbLaki.isSelected() ? "Laki-laki" : "Perempuan";

        if (id.isEmpty() || nama.isEmpty() || strTahun.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua kolom input wajib diisi!", "Error Input Kosong", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int tahunLahir = Integer.parseInt(strTahun);

            String sql = String.format(
                    "UPDATE person SET nama = '%s', tahun_lahir = %d, kategori = '%s', jenis_kelamin = '%s' WHERE id = '%s'",
                    nama, tahunLahir, kategori, jenisKelamin, id
            );

            db.insertUpdateDeleteQuery(sql);
            setTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Data berhasil diperbarui!", "Informasi", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Tahun lahir harus berupa angka!", "Error Format", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Delete Data dari tabel 'person'
    private void deleteData() {
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data pada tabel yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = tblPeople.getValueAt(selectedIndex, 0).toString();
        String nama = tblPeople.getValueAt(selectedIndex, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Apakah Anda yakin ingin menghapus data '" + nama + "' (ID: " + id + ")?",
                "Konfirmasi Hapus Data",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM person WHERE id = '" + id + "'";
            db.insertUpdateDeleteQuery(sql);
            setTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Data berhasil dihapus dari database!", "Informasi", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtNama.setText("");
        txtTahunLahir.setText("");
        txtId.setEditable(true);

        if (cbKategori.getItemCount() > 0) {
            cbKategori.setSelectedIndex(0);
        }
        rbLaki.setSelected(true);
        tblPeople.clearSelection();
        selectedIndex = -1;

        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Aplikasi Data Karyawan (PeopleData) - MySQL");
        PeopleMenu menu = new PeopleMenu();

        frame.setContentPane(menu.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}