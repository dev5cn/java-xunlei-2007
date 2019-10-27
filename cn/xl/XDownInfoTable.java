package cn.xl;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableColumnModel;

/**
 * 文件信息显示表.
 *
 */

public class XDownInfoTable
{
	private JTable downInfoTable;
	 private JScrollPane  jScrolldowninfo = new JScrollPane();
	 
	 public JScrollPane getDownInfo()
	 {
		 jScrolldowninfo.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		 jScrolldowninfo.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		 jScrolldowninfo.setBorder(BorderFactory.createEmptyBorder());
		 Object [][] obj=new Object[19][2];
		 obj[0][0]="";
		 obj[0][1]="";
		 String str []= new String []{"",""};
		
		 //一张空的表
		 downInfoTable=new newJTable(obj,str);
		 
		 downInfoTable.getTableHeader().setReorderingAllowed(false);
		 TableColumnModel tc=downInfoTable.getColumnModel();
		 
		 tc.getColumn(0).setPreferredWidth(120);//首选尺寸.
		 tc.getColumn(0).setMaxWidth(200);
		 //隐藏表头.
		 downInfoTable.setGridColor(new Color(226, 238, 249));
		 downInfoTable.setTableHeader(null);
		
		 
		 
		 jScrolldowninfo.setViewportView(downInfoTable);
		 SaveRunTime.setDOWN_INFOTABLE(downInfoTable);
		 return jScrolldowninfo;

	 }
}
