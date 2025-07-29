import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Set;

public class TelaProduto extends JFrame {
    private GenericDao<Produto> dao;
    private DefaultTableModel modelo;
    private JTable tabela;

    public TelaProduto() {
        super("Gerenciador de Produtos");

        try {
            dao = new GenericDao<>("produtos.dat");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao criar arquivo: " + e.getMessage());
        }

        setLayout(new BorderLayout());

        modelo = new DefaultTableModel(new Object[]{"Código", "Descrição", "Preço"}, 0);
        tabela = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabela);
        add(scroll, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel();
        JButton btnAdicionar = new JButton("Adicionar");
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnRemover = new JButton("Remover");

        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnRemover);
        add(painelBotoes, BorderLayout.SOUTH);

        carregarTabela();

        btnAdicionar.addActionListener(e -> adicionarProduto());
        btnRemover.addActionListener(e -> removerProduto());
        btnAtualizar.addActionListener(e -> atualizarProduto());

        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void carregarTabela() {
        modelo.setRowCount(0);
        try {
            Set<Produto> produtos = dao.getAll();
            for (Produto p : produtos) {
                modelo.addRow(new Object[]{p.getCodigo(), p.getDescricao(), p.getPreco()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + e.getMessage());
        }
    }

    private void adicionarProduto() {
        try {
            int codigo = Integer.parseInt(JOptionPane.showInputDialog("Código:"));
            String descricao = JOptionPane.showInputDialog("Descrição:");
            double preco = Double.parseDouble(JOptionPane.showInputDialog("Preço:"));

            Produto p = new Produto(codigo, descricao, preco);
            if (dao.salvar(p)) {
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Produto já existe.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void removerProduto() {
        int linha = tabela.getSelectedRow();
        if (linha >= 0) {
            int codigo = (int) modelo.getValueAt(linha, 0);
            Produto p = new Produto(codigo, "", 0);
            try {
                if (dao.remover(p)) {
                    carregarTabela();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao remover: " + e.getMessage());
            }
        }
    }

    private void atualizarProduto() {
        int linha = tabela.getSelectedRow();
        if (linha >= 0) {
            try {
                int codigo = (int) modelo.getValueAt(linha, 0);
                String novaDescricao = JOptionPane.showInputDialog("Nova descrição:");
                double novoPreco = Double.parseDouble(JOptionPane.showInputDialog("Novo preço:"));

                Produto p = new Produto(codigo, novaDescricao, novoPreco);
                if (dao.atualizar(p)) {
                    carregarTabela();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaProduto());
    }
}
