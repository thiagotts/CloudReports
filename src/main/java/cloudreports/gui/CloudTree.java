/* 
 * Copyright (c) 2010-2012 Thiago T. Sá
 * 
 * This file is part of CloudReports.
 *
 * CloudReports is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * CloudReports is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * For more information about your rights as a user of CloudReports,
 * refer to the LICENSE file or see <http://www.gnu.org/licenses/>.
 */

package cloudreports.gui;

import cloudreports.dao.CustomerRegistryDAO;
import cloudreports.dao.DatacenterRegistryDAO;
import cloudreports.models.CustomerRegistry;
import cloudreports.models.DatacenterRegistry;
import java.awt.Component;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * A subtype of {@link JTree} to be displayed in the main form of 
 * the application.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class CloudTree extends JTree{
    
    /** A tree node. */
    private DefaultMutableTreeNode cloudTreeNode;

    /** The provider tree node. */
    DefaultMutableTreeNode providerTreeNode;
    
    /** The customers tree node. */
    DefaultMutableTreeNode customersTreeNode;

    {
        if(this.treeModel.getRoot() instanceof DefaultMutableTreeNode)
            cloudTreeNode = (DefaultMutableTreeNode) this.treeModel.getRoot();

        providerTreeNode = new DefaultMutableTreeNode("Provider");
        customersTreeNode = new DefaultMutableTreeNode("Customers");
        getCloudTreeNode().add(providerTreeNode);
        getCloudTreeNode().add(customersTreeNode);

        this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        this.setCellRenderer(new CustomRenderer(
                new javax.swing.ImageIcon(getClass().getResource("/cloudreports/gui/resources/cloud.png")),
                new javax.swing.ImageIcon(getClass().getResource("/cloudreports/gui/resources/provider.png")),
                new javax.swing.ImageIcon(getClass().getResource("/cloudreports/gui/resources/costumers.png")),
                new javax.swing.ImageIcon(getClass().getResource("/cloudreports/gui/resources/dot.png"))));
    }

    /** Creates a new CloudTree. */
    public CloudTree() {
        super(new DefaultMutableTreeNode("CloudReports"));
        
        DatacenterRegistryDAO drDAO = new DatacenterRegistryDAO();
        String[] datacenterNames = drDAO.getAllDatacentersNames();
        for(String datacenterName : datacenterNames) {
            providerTreeNode.add(new DefaultMutableTreeNode(datacenterName,false));
        }
        
        CustomerRegistryDAO crDAO = new CustomerRegistryDAO();
        String[] customerNames = crDAO.getCustomersNames();
        for(String customerName : customerNames) {
            customersTreeNode.add(new DefaultMutableTreeNode(customerName,false));
        }
            
        this.expandPath(new TreePath(getCloudTreeNode()));
    }

    /** 
     * Updates the state of all CloudTree nodes.
     *
     * @since           1.0
     */     
    public void updateNodes() {
        providerTreeNode.removeAllChildren();
        customersTreeNode.removeAllChildren();
        DatacenterRegistryDAO drDAO = new DatacenterRegistryDAO();
        
        for(DatacenterRegistry dcr : drDAO.getListOfDatacenters()) {
            providerTreeNode.insert(new DefaultMutableTreeNode(dcr.getName(),false),0);
        }

        CustomerRegistryDAO crDAO = new CustomerRegistryDAO();
        for(CustomerRegistry c : crDAO.getListOfCustomers()) {
            customersTreeNode.insert(new DefaultMutableTreeNode(c.getName(),false),0);
        }
        this.expandPath(new TreePath(getCloudTreeNode()));
    }

    /** 
     * Inserts a new node in the CloudTree.
     * 
     * @param   name    the name of the node.
     * @param   type    the type of the node (0 is for provider and 1 is for
     *                  customers.
     * @since           1.0
     */     
    public boolean insertNode(String name, int type) {
        DefaultMutableTreeNode node;
        if(type==0) {
            Enumeration e = providerTreeNode.children();
            try{
                while(e.hasMoreElements()) {
                node = (DefaultMutableTreeNode) e.nextElement();
                if(node.toString().equalsIgnoreCase(name)) return false;
                }
            }
            catch(NoSuchElementException ex) {
                ex.printStackTrace();
            }
            providerTreeNode.insert(new DefaultMutableTreeNode(name,false),0);
            this.updateUI();
            return true;
        }
        else if(type==1) {
            Enumeration e = customersTreeNode.children();
            try{
                while(e.hasMoreElements()) {
                node = (DefaultMutableTreeNode) e.nextElement();
                if(node.toString().equalsIgnoreCase(name)) return false;
                }
            }
            catch(NoSuchElementException ex) {
                ex.printStackTrace();
            }
            customersTreeNode.insert(new DefaultMutableTreeNode(name,false),0);
            this.updateUI();
            return true;
        }
        return false;
    }

    /** 
     * Removes a node from the CloudTree.
     * 
     * @param   name    the name of the node.
     * @param   type    the type of the node (0 is for provider and 1 is for
     *                  customers.
     * @since           1.0
     */     
    public boolean removeNode(String name, int type) {
        DefaultMutableTreeNode node;
        if(type==0) {
            Enumeration e = providerTreeNode.children();
            try{
                while(e.hasMoreElements()) {
                    node = (DefaultMutableTreeNode) e.nextElement();
                    if(node.toString().equalsIgnoreCase(name)){
                        providerTreeNode.remove(node);
                        this.updateUI();
                        return true;
                    }
                }
            }
            catch(NoSuchElementException ex) {
                ex.printStackTrace();
            }
            return false;
        }
        else if(type==1) {
            Enumeration e = customersTreeNode.children();
            try{
                while(e.hasMoreElements()) {
                    node = (DefaultMutableTreeNode) e.nextElement();
                    if(node.toString().equalsIgnoreCase(name)) {
                        customersTreeNode.remove(node);
                        this.updateUI();
                        return true;
                    }
                }
            }
            catch(NoSuchElementException ex) {
                ex.printStackTrace();
            }
            return false;
        }
        return false;
    }

    /** 
     * Gets the current CloudTree node.
     * 
     * @return      a CLoudTree node.
     * @since       1.0
     */  
    public DefaultMutableTreeNode getCloudTreeNode() {
        return cloudTreeNode;
    }

    /**
     * An inner class that implements a custom TreeCellRenderer and inherits
     * from {@link DefaultTreeCellRenderer}.
     * 
     * @author      Thiago T. Sá
     * @since       1.0
     */    
    class CustomRenderer extends DefaultTreeCellRenderer {
        Icon icon0, icon1, icon2, icon3, icon4;

        /** Creates a new CustomRenderer. */
        public CustomRenderer(Icon icon0, Icon icon1, Icon icon2, Icon icon3) {
            this.icon0 = icon0;
            this.icon1 = icon1;
            this.icon2 = icon2;
            this.icon3 = icon3;
        }
      
        public Component getTreeCellRendererComponent(
                            JTree tree,
                            Object value,
                            boolean sel,
                            boolean expanded,
                            boolean leaf,
                            int row,
                            boolean hasFocus) {

            super.getTreeCellRendererComponent(
                            tree, value, sel,
                            expanded, leaf, row,
                            hasFocus);
            if (leaf) {
                setIcon(icon3);
            }
            else if(isCloud(value)){
                setIcon(icon0);
            }
            else if(isProvider(value)) {
                setIcon(icon1);
            }
            else {
                setIcon(icon2);
            }

            return this;
        }

        /** 
         * Checks if the node is the root node.
         *
         * @param   value   the value to be checked.
         * @since           1.0
         */             
        protected boolean isCloud(Object value) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
            if (node.toString().equalsIgnoreCase("CloudReports")) return true;
            return false;
        }

        /** 
         * Checks if the node is the providers root node.
         *
         * @param   value   the value to be checked.
         * @since           1.0
         */          
        protected boolean isProvider(Object value) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
            if (node.toString().equalsIgnoreCase("Provider")) return true;
            return false;
        }
    }
}
