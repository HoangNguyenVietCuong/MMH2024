import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.swing.filechooser.FileNameExtensionFilter;

public class DigitalSignatureTool extends JFrame {
    private JTextField publicKeyField;
    private JTextArea textInputField;
    private JTextArea signatureField;
    private JTextField filePathField;
    private JTextArea fileSignatureField;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public DigitalSignatureTool() {
        // Thiết lập layout cho frame chính
        setTitle("Phần mềm chữ kí điện tử");
        setLayout(new GridLayout(4, 1, 10, 10));

        // Dòng đầu tiên: Tạo khóa và nhập key
        JPanel keyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,20,5));
        JButton generateKeyButton = new JButton("Tạo Key");
        publicKeyField = new JTextField(30);
        JButton loadKeyButton = new JButton("Nhập Key");

        // Dòng thứ 2: Ký chữ ký trên văn bản
        JPanel textSignPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,20,5));
        textInputField = new JTextArea(2, 40);
        JButton signTextButton = new JButton("Ký");
        signatureField = new JTextArea(4, 40);

        // Dòng thứ 3: Ký chữ ký trên file
        JPanel fileSignPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,20,5));
        JButton loadFileButton = new JButton("Nhập File");
        filePathField = new JTextField(30);
        JButton signFileButton = new JButton("Ký File");

        // Dòng thứ 4: Hiển thị chữ ký
        JPanel signatureDisplayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,20,5));
        fileSignatureField = new JTextArea(4, 40);

        // Cấu hình sự kiện cho các nút
        generateKeyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateKeys();
            }
        });

        loadKeyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPrivateKey();
            }
        });

        signTextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signText();
            }
        });

        loadFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadFile();
            }
        });

        signFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signFile();
            }
        });

        // Thêm các thành phần vào các panel
        keyPanel.add(generateKeyButton);
        keyPanel.add(publicKeyField);
        keyPanel.add(loadKeyButton);
        
        textSignPanel.add(new JScrollPane(textInputField));
        textSignPanel.add(signTextButton);
        textSignPanel.add(new JScrollPane(signatureField));
        
        fileSignPanel.add(loadFileButton);
        fileSignPanel.add(filePathField);
        fileSignPanel.add(signFileButton);
        
        signatureDisplayPanel.add(new JScrollPane(fileSignatureField));

        // Thêm các panel vào frame chính
        
        add(keyPanel);
        add(textSignPanel);
        add(fileSignPanel);
        add(signatureDisplayPanel);

        // Cấu hình cửa sổ
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    // Hàm sinh cặp khóa RSA (Public và Private)
    private void generateKeys() {
        try {
            // Tạo cặp khóa RSA
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();

            // Hiển thị public key trong trường publicKeyField
            publicKeyField.setText(Base64.getEncoder().encodeToString(publicKey.getEncoded()));

            // Lưu private key vào file text trên ổ D
            FileWriter writer = new FileWriter("D:/private_key.txt");
            writer.write(Base64.getEncoder().encodeToString(privateKey.getEncoded()));
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Hàm tải private key từ file
    private void loadPrivateKey() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File selectedFile = fileChooser.getSelectedFile();
                BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
                String privateKeyString = reader.readLine();
                reader.close();

                // Chuyển đổi private key từ string
                byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);
                PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                privateKey = keyFactory.generatePrivate(spec);

                // Cập nhật lại public key
                publicKeyField.setText(Base64.getEncoder().encodeToString(privateKey.getEncoded()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // Ký chữ ký lên văn bản
    private void signText() {
        try {
            if (privateKey == null) {
                JOptionPane.showMessageDialog(this, "Private key chưa được tải!");
                return;
            }

            // Sử dụng RSA để ký chữ ký
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(privateKey);
            signature.update(textInputField.getText().getBytes());

            byte[] signedData = signature.sign();
            signatureField.setText(Base64.getEncoder().encodeToString(signedData));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Ký chữ ký lên file
    private void signFile() {
        try {
            // Kiểm tra xem private key đã được tải chưa
            if (privateKey == null) {
                JOptionPane.showMessageDialog(this, "Private key chưa được tải!");
                return;             
            }

            // Kiểm tra nếu chưa có file được tải lên
            String filePath = filePathField.getText();
            if (filePath == null || filePath.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Bạn chưa tải file lên!");
                return;
            }

            // Kiểm tra xem file có tồn tại không
            File file = new File(filePath);
            if (!file.exists()) {
                JOptionPane.showMessageDialog(this, "File không tồn tại!");
                return;
            }

            // Đọc nội dung của file để ký
            byte[] fileData = Files.readAllBytes(file.toPath());

            // Sử dụng RSA để ký
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(privateKey);
            signature.update(fileData);

            byte[] signedData = signature.sign();
            // Hiển thị chữ ký lên JTextArea
            fileSignatureField.setText(Base64.getEncoder().encodeToString(signedData));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    
    }

    // Hàm để tải file và hiển thị
    private void loadFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            // Hiển thị tên file lên ô thứ 2 trong dòng thứ 3
            filePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    public static void main(String[] args) {
        new DigitalSignatureTool();
    }
}