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
 * 左边的树
 *
 */

public class XLeftTree
{
	
	private  JScrollPane jScrollJTree = new JScrollPane();
	
	
	/**
	 * 完成树的构造.
	 * @return
	 */
	
	private JTree xLeftTree()
	{
		jScrollJTree.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollJTree.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        
        //根节点.
        final DefaultMutableTreeNode  xunlei=new DefaultMutableTreeNode("迅雷");
        //根节点,
        DefaultMutableTreeNode downNow=new DefaultMutableTreeNode("正在下载");
        DefaultMutableTreeNode downed=new DefaultMutableTreeNode("已下载");
       // DefaultMutableTreeNode gc=new DefaultMutableTreeNode("垃圾箱");

        DefaultTreeModel xunleiModel=new DefaultTreeModel(xunlei);
        xunleiModel.insertNodeInto(downNow,xunlei,xunlei.getChildCount());
        xunleiModel.insertNodeInto(downed,xunlei,xunlei.getChildCount());
        //xunleiModel.insertNodeInto(gc,xunlei,xunlei.getChildCount());

        DefaultMutableTreeNode downedSoft=new DefaultMutableTreeNode("软件");
        DefaultMutableTreeNode downedGame=new DefaultMutableTreeNode("游戏");
        DefaultMutableTreeNode downedMusic=new DefaultMutableTreeNode("音乐");
        DefaultMutableTreeNode downedMovie=new DefaultMutableTreeNode("影视");
        DefaultMutableTreeNode downedCombi=new DefaultMutableTreeNode("手机");
        DefaultMutableTreeNode downedBook=new DefaultMutableTreeNode("书籍");

        DefaultTreeModel downedModel=new DefaultTreeModel(downed);
        downedModel.insertNodeInto(downedSoft,downed,downed.getChildCount());
        downedModel.insertNodeInto(downedGame,downed,downed.getChildCount());
        downedModel.insertNodeInto(downedMusic,downed,downed.getChildCount());
        downedModel.insertNodeInto(downedMovie,downed,downed.getChildCount());
        downedModel.insertNodeInto(downedCombi,downed,downed.getChildCount());
        downedModel.insertNodeInto(downedBook,downed,downed.getChildCount());
        final JTree leftTree=new JTree(xunleiModel);
        
        //设置单元格的渲染器.
        leftTree.setCellRenderer(new TreeIcon());
        
        //树监听器,节点被选择时被调用.
        leftTree.addTreeSelectionListener(new TreeSelectionListener()
		{
			//得到被选中的节点后最后一个节点.用于显示下载信息或已下载信息.
			public void valueChanged(TreeSelectionEvent e)
			{
				DefaultMutableTreeNode dd=(DefaultMutableTreeNode)e.getPath().getLastPathComponent();
				
				if("已下载".equals(dd.toString()))
				{
					//已下载表.
					JScrollPane jsced=SaveRunTime.getJSC_ED();
					//滚动条组件.
					JSplitPane jsp=SaveRunTime.getJSP();
					//上面组件高度.这里需要重新被设置.
					jsp.setDividerLocation(200);
					//载入.
					jsp.setLeftComponent(jsced);
				}
				else if("正在下载".equals(dd.toString()))
				{
					//正在下载表.
					JScrollPane jscing=SaveRunTime.getJSC_ING();
					
					JSplitPane jsp=SaveRunTime.getJSP();
					jsp.setDividerLocation(200);
					jsp.setLeftComponent(jscing);//被显示.
				}
			}
		
		});
        return leftTree;
	}
	
	
	/**
	 * 树被加载到容器.
	 * @param jp
	 */
	 public void xLeftTree(JPanel jp)
	 {
		 jScrollJTree.setViewportView(this.xLeftTree());
		 jp.add(jScrollJTree);
		 jScrollJTree.setBounds(0, 130, 110, 310);
	 }
	
	 
	 
	/**
	 * 类继承自DefaultTreeCellRenderer
	 * 重写DefaultTreeCellRenderer的getTreeCellRendererComponent()方法.
	 * 为不同的节点添加不同的图片.
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
			{        //叶节点的图标
				setIcon(new ImageIcon(getClass().getResource("/image/eddown.png")));
			}
			else
			{       //有扩展的非根节点
				if(expanded)   
					setIcon(new ImageIcon(getClass().getResource("/image/xunlei.png")));   
				else   
					setIcon(new ImageIcon(""));   
			}
			return this;   
		} 
	}

	
	
}
