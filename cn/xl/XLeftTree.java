package cn.xl;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;


/**
 * ��ߵ���
 *
 */

public class XLeftTree
{
	
	private  JScrollPane jScrollJTree = new JScrollPane();
	
	
	/**
	 * ������Ĺ���.
	 * @return
	 */
	
	private JTree xLeftTree()
	{
		jScrollJTree.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollJTree.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        
        //���ڵ�.
        final DefaultMutableTreeNode  xunlei=new DefaultMutableTreeNode("Ѹ��");
        //���ڵ�,
        DefaultMutableTreeNode downNow=new DefaultMutableTreeNode("��������");
        DefaultMutableTreeNode downed=new DefaultMutableTreeNode("������");
       // DefaultMutableTreeNode gc=new DefaultMutableTreeNode("������");

        DefaultTreeModel xunleiModel=new DefaultTreeModel(xunlei);
        xunleiModel.insertNodeInto(downNow,xunlei,xunlei.getChildCount());
        xunleiModel.insertNodeInto(downed,xunlei,xunlei.getChildCount());
        //xunleiModel.insertNodeInto(gc,xunlei,xunlei.getChildCount());

        DefaultMutableTreeNode downedSoft=new DefaultMutableTreeNode("���");
        DefaultMutableTreeNode downedGame=new DefaultMutableTreeNode("��Ϸ");
        DefaultMutableTreeNode downedMusic=new DefaultMutableTreeNode("����");
        DefaultMutableTreeNode downedMovie=new DefaultMutableTreeNode("Ӱ��");
        DefaultMutableTreeNode downedCombi=new DefaultMutableTreeNode("�ֻ�");
        DefaultMutableTreeNode downedBook=new DefaultMutableTreeNode("�鼮");

        DefaultTreeModel downedModel=new DefaultTreeModel(downed);
        downedModel.insertNodeInto(downedSoft,downed,downed.getChildCount());
        downedModel.insertNodeInto(downedGame,downed,downed.getChildCount());
        downedModel.insertNodeInto(downedMusic,downed,downed.getChildCount());
        downedModel.insertNodeInto(downedMovie,downed,downed.getChildCount());
        downedModel.insertNodeInto(downedCombi,downed,downed.getChildCount());
        downedModel.insertNodeInto(downedBook,downed,downed.getChildCount());
        final JTree leftTree=new JTree(xunleiModel);
        
        //���õ�Ԫ�����Ⱦ��.
        leftTree.setCellRenderer(new TreeIcon());
        
        //��������,�ڵ㱻ѡ��ʱ������.
        leftTree.addTreeSelectionListener(new TreeSelectionListener()
		{
			//�õ���ѡ�еĽڵ�����һ���ڵ�.������ʾ������Ϣ����������Ϣ.
			public void valueChanged(TreeSelectionEvent e)
			{
				DefaultMutableTreeNode dd=(DefaultMutableTreeNode)e.getPath().getLastPathComponent();
				
				if("������".equals(dd.toString()))
				{
					//�����ر�.
					JScrollPane jsced=SaveRunTime.getJSC_ED();
					//���������.
					JSplitPane jsp=SaveRunTime.getJSP();
					//��������߶�.������Ҫ���±�����.
					jsp.setDividerLocation(200);
					//����.
					jsp.setLeftComponent(jsced);
				}
				else if("��������".equals(dd.toString()))
				{
					//�������ر�.
					JScrollPane jscing=SaveRunTime.getJSC_ING();
					
					JSplitPane jsp=SaveRunTime.getJSP();
					jsp.setDividerLocation(200);
					jsp.setLeftComponent(jscing);//����ʾ.
				}
			}
		
		});
        return leftTree;
	}
	
	
	/**
	 * �������ص�����.
	 * @param jp
	 */
	 public void xLeftTree(JPanel jp)
	 {
		 jScrollJTree.setViewportView(this.xLeftTree());
		 jp.add(jScrollJTree);
		 jScrollJTree.setBounds(0, 130, 110, 310);
	 }
	
	 
	 
	/**
	 * ��̳���DefaultTreeCellRenderer
	 * ��дDefaultTreeCellRenderer��getTreeCellRendererComponent()����.
	 * Ϊ��ͬ�Ľڵ���Ӳ�ͬ��ͼƬ.
	 *
	 */
	private class TreeIcon extends DefaultTreeCellRenderer
	{
		private static final long serialVersionUID = 6744125992231881380L;

		public Component getTreeCellRendererComponent(JTree tree,Object value,
				boolean selected,boolean expanded,boolean isleaf,int row,boolean hasFocus)
		{   
			super.getTreeCellRendererComponent(tree,value,selected,expanded,isleaf,row,hasFocus);   
			if(isleaf)
			{        //Ҷ�ڵ��ͼ��
				setIcon(new ImageIcon(getClass().getResource("/image/eddown.png")));
			}
			else
			{       //����չ�ķǸ��ڵ�
				if(expanded)   
					setIcon(new ImageIcon(getClass().getResource("/image/xunlei.png")));   
				else   
					setIcon(new ImageIcon(""));   
			}
			return this;   
		} 
	}

	
	
}
