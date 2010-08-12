package cvsstructure.gui;

import cvsstructure.instalador.CheckNode;
import cvsstructure.instalador.CheckRenderer;
import cvsstructure.instalador.model.InterfacesFactory;
import cvsstructure.instalador.model.LayoutInterfaces;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Instalador.java
 *
 * Created on 10/03/2010, 21:12:41
 */


/**
 *
 * @author andrein
 */
public class SfwInstaladorFrame extends javax.swing.JFrame {

    private DefaultTreeModel model;
    private DefaultMutableTreeNode top;
    private Collection<LayoutInterfaces> layoutInterfasCollection = new ArrayList();
    private static CheckNode lastPathCompnent;
    private InterfacesFactory interfacesFactory = new InterfacesFactory();
    private String[][] dados = null;
    private String[] colunas;
    private DefaultTableModel modelo;

    /** Creates new form Instalador */
    public SfwInstaladorFrame() {
        //ResourceMap resourceMap = ((SfwInstaladorScriptApp)Application.getInstance(SfwInstaladorScriptApp.class)).getContext().getResourceMap(SfwWizardWelcome.class);
        //ResourceMap resourceMap = ((SfwInstaladorScriptApp)Application.getInstance(SfwInstaladorScriptApp.class)).getContext().getResourceMap(SfwInstaladorFrame.class);
        //String teste = resourceMap.getString("jLabel5.text", new Object[0]);

        //DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root Label");
        //root.add(new DefaultMutableTreeNode("Node Label"));


        //DefaultTreeModel model = (DefaultTreeModel) jTree.getModel();
        //DefaultTreeModel model = new DefaultTreeModel(root);
        //model.insertNodeInto(root, root, 1);

        //jTree = new JTree(root);
        //jTree.setModel(model);

        //this.addWindowListener(new WindowAdapter() {
        //    @Override
        //    public void windowClosing(WindowEvent e) {System.exit(0);}
        //});
        
        //top = new DefaultMutableTreeNode("Objetos Interfaces");
        top = new CheckNode("Objetos Interfaces");

        //createNodes(top);
        model = new DefaultTreeModel(top);
        
        jTree = new JTree(top);
        jTree.setModel(model);
        jTree.setCellRenderer(new CheckRenderer());
        jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTree.putClientProperty("JTree.lineStyle", "Angled");
        //jTree.addMouseListener(new NodeSelectionListener(jTree));

         //Listen for when the selection changes.
        //jTree.addTreeSelectionListener((TreeSelectionListener) new NodeSelectionListener(jTree));

        //JScrollPane treeView = new JScrollPane(jTree);
        jTree.updateUI();


        //
        initComponents();
        //jTableObjetosAcessados = new TableModel();

        dados = new String [][]{
            {"",""}
        };

        colunas = new String []{"Objeto Acessado", "Caminho"};

        // Ao inves de passar direto, colocamos os dados em um modelo
        modelo = new DefaultTableModel(colunas, 0);

        // e passamos o modelo para criar a jtable
        jTableObjetosAcessados.setModel( modelo );
        this.habilitaComponentes(false);
    }

    private void habilitaComponentes(boolean status){
        btInserirObjAcessado.setEnabled(status);
        btRemoveObjAcessado.setEnabled(status);
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jScrollPane1 = new javax.swing.JScrollPane();
        jTree = new javax.swing.JTree(top);
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txCaminhoObjeto = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txPastaInterace = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableObjetosAcessados = new javax.swing.JTable();
        btInserirObjAcessado = new javax.swing.JToggleButton();
        btRemoveObjAcessado = new javax.swing.JToggleButton();
        jTextField2 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btInstalarPacote = new javax.swing.JToggleButton();
        jTextField4 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jToggleButton2 = new javax.swing.JToggleButton();
        jTextField3 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jToggleButton1 = new javax.swing.JToggleButton();
        jTextField1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jTree.setAutoscrolls(true);
        jTree.setDragEnabled(true);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jTree, org.jdesktop.beansbinding.ELProperty.create("${model}"), jTree, org.jdesktop.beansbinding.BeanProperty.create("model"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(jTree);

        jLabel3.setText("Objeto acessado:");

        jLabel2.setText("Caminho Objeto:");

        jLabel1.setText("Pasta Interface:");

        jTableObjetosAcessados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                "Título 1", "Título 2"
            }
        ));
        jTableObjetosAcessados.setEnabled(false);
        jScrollPane2.setViewportView(jTableObjetosAcessados);

        btInserirObjAcessado.setText("inserir");
        btInserirObjAcessado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btInserirObjAcessadoActionPerformed(evt);
            }
        });

        btRemoveObjAcessado.setText("remover");
        btRemoveObjAcessado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRemoveObjAcessadoActionPerformed(evt);
            }
        });

        jLabel5.setText("Ordem:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(btRemoveObjAcessado)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(btInserirObjAcessado)))
                    .addComponent(jLabel2)
                    .addComponent(txCaminhoObjeto, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txPastaInterace, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField2)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txPastaInterace, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txCaminhoObjeto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btInserirObjAcessado)
                    .addComponent(btRemoveObjAcessado))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Dados Objeto", jPanel1);

        btInstalarPacote.setText("Instalar Pacote");

        jTextField4.setText("jTextField4");

        jLabel7.setText("jLabel7");

        jTextField5.setText("jTextField5");

        jLabel8.setText("jLabel8");

        jTextField6.setText("jTextField6");

        jLabel9.setText("jLabel9");

        jTextField7.setText("jTextField7");

        jLabel10.setText("jLabel10");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(365, Short.MAX_VALUE)
                .addComponent(btInstalarPacote)
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(163, 163, 163)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(131, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 172, Short.MAX_VALUE)
                .addComponent(btInstalarPacote)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Base Instalação", jPanel2);

        jToggleButton2.setText("Gerar Pacote");

        jLabel6.setText("Local onde os scripts serão gerados:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextField3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE)
                            .addComponent(jToggleButton2))
                        .addGap(24, 24, 24))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addContainerGap(295, Short.MAX_VALUE))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 246, Short.MAX_VALUE)
                .addComponent(jToggleButton2)
                .addGap(19, 19, 19))
        );

        jTabbedPane1.addTab("Gerar Pacote", jPanel3);

        jToggleButton1.setText("Carregar dados");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        jTextField1.setText(".\\Pacotes");

        jLabel4.setText("Local dos scripts para geração do pacote:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jToggleButton1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jToggleButton1)
                        .addGap(12, 12, 12))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE))
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        
        // diretório inicial para gerar a ordem de instalção
        //sCaminhaGeracao = jframe.getTxCaminhaGeracao();
        //if (sCaminhaGeracao.equals(".\\")){
        //    this.path = new File(".").getCanonicalPath();
        //}else{
        //    if(sCaminhaGeracao.substring(sCaminhaGeracao.length()-1).equals("\\")){
        //        this.path = sCaminhaGeracao.substring(0, sCaminhaGeracao.length()-1);
        //    }else{
        //        this.path = sCaminhaGeracao;
        //    }
        //}

        String nomeDir = "";
        try {
            nomeDir = new File(".").getCanonicalPath() + "\\pacotes\\";
        } catch (IOException ex) {
            Logger.getLogger(SfwInstaladorFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        StringBuffer dirAtual = new StringBuffer();
        dirAtual.append(".\\" + nomeDir.substring(nomeDir.lastIndexOf("\\")+1, nomeDir.length()) + "\\" );

        File diretorio = new File(nomeDir);
        File[] subdiretorios = diretorio.listFiles();
        for(File subdir : subdiretorios){
            if(subdir.isDirectory()){
                // listando subdiretórios
                CheckNode checkNode = new CheckNode(subdir.getName());
                createNodes(top, checkNode);
                listaSubDir(subdir, dirAtual + subdir.getName() + "\\", checkNode);

            }
        }

        jTree.setCellRenderer(new CheckRenderer());
        //jTree.putClientProperty("JTree.lineStyle", "Angled");
        jTree.addMouseListener(new NodeSelectionListener(jTree));

    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void btRemoveObjAcessadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRemoveObjAcessadoActionPerformed
        // Obtem a linha selecionada na tabela e chama o método
        // para excluir a linha
        int linhaSelecionada = jTableObjetosAcessados.getSelectedRow();

        // Verificamos se existe realmente alguma linha selecionada
        if( linhaSelecionada < 0 ){
            return;
        }else{
            // Remove a linha do modelo
            //formulaController.addCollectionFormulaRemoved(getModelo().getValueAt(linhaSelecionada, 0).toString());
            //formulaController.removerCollectionFormula(getModelo().getValueAt(linhaSelecionada, 0).toString(), getModelo().getValueAt(linhaSelecionada, 1).toString());
            //System.out.println(getModelo().getValueAt(linhaSelecionada, 0).toString());
            ((DefaultTableModel)jTableObjetosAcessados.getModel()).removeRow(linhaSelecionada);
        }
}//GEN-LAST:event_btRemoveObjAcessadoActionPerformed

    private void btInserirObjAcessadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btInserirObjAcessadoActionPerformed
        String[] row = new String []{"bla","bla"};
        ((DefaultTableModel)jTableObjetosAcessados.getModel()).addRow(row);
    }//GEN-LAST:event_btInserirObjAcessadoActionPerformed

	/**************************************************************************
	 * <b>Listar subDiretórios</b>
	 * @param subDir
	 **************************************************************************/
    private void listaSubDir(File subDir, String pDirAtual, CheckNode ptop) {
        //ArrayList listaStrOut = new ArrayList();
        File[] subdiretorios = subDir.listFiles();

        for(File subdir : subdiretorios){
            StringBuffer dirAtual = new StringBuffer();
            dirAtual.append(pDirAtual);
            if(subdir.isDirectory()){
                if(!subdir.getName().equals("CVS")){
                    CheckNode checkNode = new CheckNode(subdir.getName());
                    createNodes(ptop, checkNode);
                    dirAtual.append(subdir.getName() + "\\");
                    listaSubDir(subdir, dirAtual.toString(), checkNode);
                }
            }else{
                if(subdir.getName().toString().endsWith(".sql")
                        && !subdir.getName().toString().endsWith("ordem_instalacao.sql")){
                    //jTextArea1.append(dirAtual + subdir.getName() + "" + sQuebraLinha);
                    CheckNode checkNode = new CheckNode(subdir.getName());
                    createNodes(ptop, checkNode);

                    // Criando Objeto
                    LayoutInterfaces layoutInterfaces = new LayoutInterfaces();
                    layoutInterfaces.setDescInterface(subdir.getName());
                    layoutInterfaces.setObjeto(subdir.getName());

                    // Adicionando na Lista
                    layoutInterfasCollection.add(layoutInterfaces);

                }
            }
        }
    }

    private void createNodes(DefaultMutableTreeNode ptop, CheckNode checkNode) {
        //DefaultMutableTreeNode category = null;
        //DefaultMutableTreeNode book = null;

        //category = new DefaultMutableTreeNode("Books for Java Programmers");
        //top.add(category);

        //original Tutorial
        //book = new DefaultMutableTreeNode("The Java Tutorial: A Short Course on the Basics tutorial.html");
        //category.add(book);

        //----------------------
        //createNodes(top);
        //TreePath tp = jTree.getSelectionPath();
        //MutableTreeNode insertNode = (MutableTreeNode) tp.getLastPathComponent();
        MutableTreeNode insertNode = ptop;
        //MutableTreeNode insertNode = (MutableTreeNode)model.getRoot();
        int insertIndex = 0;
        if (insertNode.getParent() != null) {
          //MutableTreeNode parent = (MutableTreeNode) insertNode.getParent();
          MutableTreeNode parent = ptop; //forcei ser o pai
          insertIndex = parent.getIndex(insertNode) + 1;
          insertNode = parent;
        }
        //MutableTreeNode node = new DefaultMutableTreeNode("UM DESCRIÇÂO QUALQUER");
        //CheckNode checkNode = new CheckNode("UM DESCRIÇÂO QUALQUER");
        checkNode.setSelectionMode(CheckNode.DIG_IN_SELECTION);
        MutableTreeNode node = (MutableTreeNode)checkNode;
        getModel().insertNodeInto(node, insertNode, insertIndex);

    }

    public void getLastPathComponent(CheckNode node){
        SfwInstaladorFrame.lastPathCompnent = lastPathCompnent;
        //CheckNode node = (CheckNode)lastPathCompnent;

        StringBuffer sbNodes = new StringBuffer();
        this.habilitaComponentes(false);
        this.jTableObjetosAcessados.setEnabled(false);

        if(node.getUserObject().toString().substring(node.getUserObject().toString().length()-3,
                                                     node.getUserObject().toString().length()
                                                     ).toUpperCase().equals("SQL")
        ){

            this.habilitaComponentes(true);
            this.jTableObjetosAcessados.setEnabled(true);
          //Enumeration enumf = node.breadthFirstEnumeration();
          //while (enumf.hasMoreElements()) {
          //CheckNode nodeEl = (CheckNode)enumf.nextElement();
            //if (node.isSelected()) {
                TreeNode[] nodes = node.getPath();
                //textArea.append("\n" + nodes[0].toString());
                for (int i=1;i<nodes.length;i++) {
                    //textArea.append("/" + nodes[i].toString());
                    sbNodes.append("/" + nodes[i].toString());
                }

            //}
            txCaminhoObjeto.setText(sbNodes.toString());
            txPastaInterace.setText(nodes[1].toString());
            /*
            CheckNode node2;
            if(node.getParent() != null){
                node2 = (CheckNode) node.getParent();
                if(node2.getParent() != null){
                    String a = node2.getUserObject().toString();
                    node2 = (CheckNode) node2.getParent();
                    if(node2.getParent() != null){
                        String b = node2.getUserObject().toString();
                    }
                }
            }
            */
        }
    }

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SfwInstaladorFrame().setVisible(true);

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btInserirObjAcessado;
    private javax.swing.JToggleButton btInstalarPacote;
    private javax.swing.JToggleButton btRemoveObjAcessado;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableObjetosAcessados;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JToggleButton jToggleButton2;
    private javax.swing.JTree jTree;
    private javax.swing.JTextField txCaminhoObjeto;
    private javax.swing.JTextField txPastaInterace;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the model
     */
    public DefaultTreeModel getModel() {
        return model;
    }

    /**
     * @param model the model to set
     */
    public void setModel(DefaultTreeModel model) {
        this.model = model;
    }

  class NodeSelectionListener extends MouseAdapter {
    JTree tree;

    NodeSelectionListener(JTree tree) {
      this.tree = tree;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
      int x = e.getX();
      int y = e.getY();
      int row = jTree.getRowForLocation(x, y);
      TreePath  path = jTree.getPathForRow(row);
      //TreePath  path = tree.getSelectionPath();
      if (path != null) {
        CheckNode node = (CheckNode)path.getLastPathComponent();
        boolean isSelected = ! (node.isSelected());
        node.setSelected(isSelected);
        if (node.getSelectionMode() == CheckNode.DIG_IN_SELECTION) {
          if ( isSelected ) {
            jTree.expandPath(path);
          } else {
            jTree.collapsePath(path);
          }
        }
        ((DefaultTreeModel)jTree.getModel()).nodeChanged(node);
        // I need revalidate if node is root.  but why?
        if (row == 0) {
          jTree.revalidate();
          jTree.repaint();
        }

        // Setando a TextFild
        //node.getRoot().toString();
        getLastPathComponent(node);
        //new SfwInstaladorFrame().getLastPathCompnent((MutableTreeNode) tree.getLastSelectedPathComponent());
      } // end mouseClicked

    } // end NodeSelectionListener

} // end SfsInstalador



/*
  class ButtonActionListener implements ActionListener {
    CheckNode root;
    JTextArea textArea;

    ButtonActionListener(final CheckNode root,
                         final JTextArea textArea) {
      this.root     = root;
      this.textArea = textArea;
    }

    public void actionPerformed(ActionEvent e) {
      Enumeration enumf = root.breadthFirstEnumeration();
      while (enumf.hasMoreElements()) {
        CheckNode node = (CheckNode)enumf.nextElement();
        if (node.isSelected()) {
          TreeNode[] nodes = node.getPath();
          textArea.append("\n" + nodes[0].toString());
          for (int i=1;i<nodes.length;i++) {
            textArea.append("/" + nodes[i].toString());
          }
        }
      }
    }
  }
 * */
  }
