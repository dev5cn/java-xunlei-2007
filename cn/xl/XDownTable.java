package cn.xl;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;


/**
 *用于显示下载任务的表格.
 *
 */
public class XDownTable
{
	private static final long serialVersionUID = 2621790669412858341L;
	private JTable downTable;
	
	/**
	 * 行索引.
	 */
	private static int rowIndex=0;
	

	/**
	 * 构造一张表,用于显示下载任务运行状态.
	 * @return
	 */
	public JTable xDownTable() 
	{
		//得到表.
		downTable=new XDownTable().createJTable(this.getVector(),1);
	
		//表可以被改变宽度,但不能拖走.
		downTable.getTableHeader().setReorderingAllowed(false);
		
		//列的宽度控制.//525宽
		TableColumnModel tc=downTable.getColumnModel();
		
		tc.getColumn(0).setPreferredWidth(50);
		tc.getColumn(0).setMinWidth(30);
		tc.getColumn(1).setPreferredWidth(210);
		tc.getColumn(1).setMinWidth(120);
		tc.getColumn(2).setPreferredWidth(70);
		tc.getColumn(2).setMinWidth(30);
		tc.getColumn(3).setMinWidth(30);
		tc.getColumn(4).setMinWidth(50);
		tc.getColumn(5).setMinWidth(50);
		tc.getColumn(6).setPreferredWidth(80);
		tc.getColumn(6).setMinWidth(60);
		
		
		
		//鼠标事件.
		downTable.addMouseListener(new MouseAdapter()
		{
			//行内双击事件.
			public void mouseClicked(MouseEvent e)
			{
				if(e.getButton()==3) return;//过滤右键
				if(e.getClickCount()==2)//得到鼠标双击事件.
				{
					//得到被选择的行.
					if(rowIndex>=SaveRunTime.getLIST().size())
					{
						System.out.println("点到了空白行.");
						return;
					}
					else
					{	//得到线程锁.
						DownLoad downLoad=(DownLoad)SaveRunTime.getLIST().get(rowIndex);
						//如果线程正在进行.则让其进入等待状态.
						if(downLoad.getDownLoadSign()==1)
						{	
							//暂停按钮被禁用.
							SaveRunTime.getPAUSE().setEnabled(false);
							downLoad.setDownLoadSign(0);//通知线程暂停.
							//不在下载线程中设置图标.
							downTable.setValueAt(new XDownTable().getImageIcon("/image/dpause.png"), rowIndex, 0);
						}
						//否则进入暂停.
						else if(downLoad.getDownLoadSign()==0)
						{	
							synchronized(downLoad)
							{
								downLoad.notify();
								SaveRunTime.getPAUSE().setEnabled(true);
								downTable.setValueAt(new XDownTable().getImageIcon("/image/ing.png"), rowIndex, 0);
								//线程被唤醒后会设置自己的开关变量.
							}
						}
						//否则重新启动一个线程.一个异常中断的线程,或是一个历史任务.
						else if(downLoad.getDownLoadSign()==-1||downLoad.getDownLoadSign()==-3)	
						{
							//得到文件路径与文件名.
							String saveAs=downLoad.getDownLoadFileName();
							String choos=downLoad.getDownLoadFileDirectory();
							String url=downLoad.getURL(downLoad);//得到链接路径.
							
							//得到断点.
							long point=new XDownTable().TestFile(choos+saveAs);
							//重新启动一个测试线程.
							new Thread(new TestLink(downLoad.row,url,choos,saveAs,point)).start();
						}
						
					}
				}
			}
		
			//行内右击事件.只在已经存在任务的行内有效.
			public void mousePressed(MouseEvent ex)
			{
				if(ex.getButton()==1)//过滤左键.
				return;
				
				Point point=new Point(ex.getX(),ex.getY());	//获得坐标点,
				final int row=downTable.rowAtPoint(point);	//根据坐标点获得行的索引.	
				
				
				downTable.requestFocus();//行获得焦点.
				downTable.setRowSelectionInterval(0, row);
				
				JPopupMenu jpop=new JPopupMenu();		 //准备弹出一个右键菜单.
				JMenuItem start=new JMenuItem("开始");
				
				JMenuItem newTask=new JMenuItem("新建");
				newTask.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						SaveRunTime.getNEWTASK_JDIALOG().setVisible(true);
					}
				});
				
				
				JMenuItem del=new JMenuItem("删除");
				
				//删除按钮单击事件.
				del.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						//删除一个任务需要,删除这一行所对应的下载线程锁,计时线程锁.
						DownLoad downLoad=(DownLoad)(SaveRunTime.getLIST().get(row));
						
						//如果此线程正在运行.
						if(downLoad.getDownLoadSign()==1)
						{
							downLoad.setDownLoadSign(-2);//下载线程调用删除.
						}
						else//线程处于睡眠或中断状态时.直接删除.
						{
							try
							{
								downLoad.taskEnd(row);
								new XmlOperation().writerTaskInfo();//写xml.
							} catch (Exception ex)
							{
								ex.printStackTrace();
							}
						}
					}
				});
				
				
				//开始按钮单击事件.(未完成)
				start.addActionListener(new ActionListener()
				{
					//判断线程的运行状态.
					public void actionPerformed(ActionEvent e)
					{	//得到给定行的线程对象.
						DownLoad downLoad=(DownLoad)(SaveRunTime.getLIST().get(row));
						int sign=downLoad.getDownLoadSign();//线程状态.
						if(sign==-1)//线程异常终止状态.重新启动一个线程.
						{
							//得到文件路径
							String saveAs=downLoad.getDownLoadFileName();
							String choos=downLoad.getDownLoadFileDirectory();
							String url=downLoad.getURL(downLoad);//得到链接路径.
							//得到断点.
							long point=new XDownTable().TestFile(choos+saveAs);
							
							//重新启动一个测试线程.
							new Thread(new TestLink(downLoad.row,url,choos,saveAs,point)).start();
						}
					}
				});
				jpop.setPopupSize(60, 80); 
				jpop.add(start);
				jpop.addSeparator();
				jpop.add(newTask);
				jpop.addSeparator();
				jpop.add(del);
											//菜单弹出坐标点.
				jpop.setLocation(ex.getX()+166,ex.getY()+180);
				jpop.setVisible(true);
				
			}	
		});
		
		
		
		
		
		
		//监听器,当选择列时调用.用于显示任务信息.
		DefaultListSelectionModel  listSM=(DefaultListSelectionModel)downTable.getSelectionModel();
		listSM.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		listSM.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				//源.
				ListSelectionModel lm=(ListSelectionModel)e.getSource();
				
				if(e.getValueIsAdjusting())
				{
					//过滤一次响应.
					return;
				}
				//得到被选择的行.
				rowIndex=lm.getLeadSelectionIndex();
				rowIndex=lm.getLeadSelectionIndex();
				if(rowIndex>SaveRunTime.getSAVE_HASHMAP().size()-1)
				{//如果选到空白行,直接返回.
					return;
				}
				
				HashMap hm=SaveRunTime.getSAVE_HASHMAP().get(rowIndex);
				JTable xdit=SaveRunTime.getDOWN_INFOTABLE();
				xdit.setValueAt("正在获取链接...",0,0);	
				xdit.setValueAt("",0,1);
				xdit.setValueAt("文件名称",1,0);
				xdit.setValueAt(hm.get("文件名称"),1,1);
				xdit.setValueAt("文件长度",2,0);
				xdit.setValueAt(hm.get("文件长度"),2,1);
				xdit.setValueAt("文件类型",3,0);
				xdit.setValueAt(hm.get("文件类型"),3,1);
				xdit.setValueAt("保存路径",4,0);
				xdit.setValueAt(hm.get("保存路径"),4,1);
				xdit.setValueAt("链接地址",5,0);
				xdit.setValueAt(hm.get("链接地址"),5,1);
				xdit.setValueAt("对方主机",6,0);
				xdit.setValueAt(hm.get("对方主机"),6,1);
				return;
			}
		});
		
		 //网格颜色.
		downTable.setGridColor(new Color(226, 238, 249));
		
		//表头.
		JTableHeader downTableHeader= downTable.getTableHeader();
		
		//表头有下边框
		downTableHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.gray));
		
		//构造单元格的渲染器.
		DefaultTableCellRenderer dtc=new DefaultTableCellRenderer();
		
		//设置背景色与容器背景色一致.
		dtc.setBackground(new Color(226, 238, 249));
		//将表头设置成此渲染器.
		downTableHeader.setDefaultRenderer(dtc);
		//保存对象.
		SaveRunTime.setDOWN_TABLE(downTable);
		return downTable;
	}
	
	
	/**
	 * 返回一个设置完成的JScrollPane
	 * @param table
	 * @return
	 */
	public JScrollPane getJScrollPane(JTable table)
	{
		JScrollPane jScrollTable = new JScrollPane();
		//滚动容器的基本设置
		jScrollTable.setBackground(new java.awt.Color(255, 255, 255));
        jScrollTable.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollTable.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        //滚动条没有边框.
        jScrollTable.setBorder(BorderFactory.createEmptyBorder());
		//表格所在的滚动容器背景色.
		jScrollTable.setBackground(new Color(226, 238, 249));
		//载入表格.
		jScrollTable.setViewportView(table);
		
		//抛出滚动容器.
		return jScrollTable;
	}
		
	
	/**
	 * 已下载任务信息表.
	 * @return
	 */
	public JTable taskComplete()
	{
		//空的表.
		JTable downLoadInfo=new XDownTable().createJTable(this.getNullVector(),0);
	
		//表可以被改变宽度,但不能拖走.
		downLoadInfo.getTableHeader().setReorderingAllowed(false);
		TableColumnModel tc=downLoadInfo.getColumnModel();
		
		//列的宽度控制.//525宽
		tc.getColumn(0).setPreferredWidth(50);
		tc.getColumn(0).setMinWidth(30);
		tc.getColumn(1).setPreferredWidth(210);
		tc.getColumn(1).setMinWidth(120);
		tc.getColumn(2).setPreferredWidth(70);
		tc.getColumn(2).setMinWidth(30);
		tc.getColumn(3).setMinWidth(30);
		tc.getColumn(4).setMinWidth(50);
		tc.getColumn(5).setMinWidth(50);
		tc.getColumn(6).setPreferredWidth(80);
		tc.getColumn(6).setMinWidth(60);
		
		
		downLoadInfo.setGridColor(new Color(226, 238, 249));
		
		//表头.
		JTableHeader downTableHeader= downLoadInfo.getTableHeader();
		
		//表头有下边框
		downTableHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.gray));
		
		//构造单元格的渲染器.
		DefaultTableCellRenderer dtc=new DefaultTableCellRenderer();
		
		//设置背景色与容器背景色一致.
		dtc.setBackground(new Color(226, 238, 249));
		//将表头设置成此渲染器.
		downTableHeader.setDefaultRenderer(dtc);
		
		//保存对象.
		SaveRunTime.setDOWN_INFOTABLE(downLoadInfo);
		return downLoadInfo;
	}
	
	
	
	/**
	 * 构造一张表 18.7 type值为0或者1,为0时传递一个空的Vector.
	 */
	@SuppressWarnings("unchecked")
	public JTable createJTable(Vector<Vector> vc,int type)
	{
		Object[][]obj=new Object[18][7];
		String [] str1= new String []{"状态", "文件名称", "进度", "速度","剩余时间", "用时", "文件类型",};
		
		if(type==1)
		{
			for(int i=0;i<vc.size();i++)
			{
				Vector<Object> vcc=(Vector<Object>)vc.get(i);
				if("-3".equals(vcc.get(0)))
				{	
					vcc.remove(0);
					vcc.insertElementAt(this.getImageIcon("/image/dpause.png"), 0);
				}
				else if("-1".equals(vcc.get(0)))
				{
					vcc.remove(0);
					vcc.insertElementAt(this.getImageIcon("/image/die.png"), 0);
				}
				vcc.trimToSize();
				String str="";//避免等于null
				if("".equals(vcc.get(5))==false)
				{//避免""解析时抛出异常.
					str=new DownLoad().new CounterThread().getTime(Long.parseLong((String)vcc.get(5)));
				}
				obj[i][0]=vcc.get(0);
				obj[i][1]=vcc.get(1);
				obj[i][2]=vcc.get(2);
				obj[i][3]=vcc.get(3);
				obj[i][4]=vcc.get(4);
				obj[i][5]=str;
				obj[i][6]=vcc.get(6);	//表格只得到Vector的前七个值.
			}
		}
		else if(type==0)
		{
			Vector<Object> vcc=(Vector<Object>)vc.get(0);
			obj[0][0]=vcc.get(0);
			obj[0][1]=vcc.get(1);
			obj[0][2]=vcc.get(2);
			obj[0][3]=vcc.get(3);
			obj[0][4]=vcc.get(4);
			obj[0][5]=vcc.get(5);
			obj[0][6]=vcc.get(6);
		}
		return new newJTable(obj,str1);
	}
	
	
	
	/**
	 * 加载从xml文件中得到的Vector,如果为空,则返回一个带 "" 的Vector
	 * 程序最早运行时由getJTable()调用.
	 * @return
	 */
	public Vector<Vector> getVector()
	{
		try
		{	//读取xml文件.
			Vector<Vector> V=new XmlOperation().readTaskInfo();
			if(V.size()!=0)
			{
				SaveRunTime.getNEWTASK().setRow(V.size());//行索引值等于历史记录个数.
				SaveRunTime.setXML_VC(V);
				this.loadHistoryTask(V); //加载历史记录.
				return V;
			}
			else//可能没有历史记录.
			{
				return this.getNullVector();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			//读xml文件得到异常时,也返回一个空的Vector
			return this.getNullVector();
		}
	}
	
	
	
	/**
	 * 加载历史记录.程序最早运行时由getVecotr()调用.
	 * @param V
	 */
	private void loadHistoryTask(Vector<Vector> V)
	{
		
		//加载历史任务,并为集合类中添加锁.
		List<Object> list=SaveRunTime.getLIST();
		List<Object> timeList=SaveRunTime.getTIMELIST();
		List<HashMap<String,String>> testHashMap=SaveRunTime.getSAVE_HASHMAP();
		for(int i=0;i<V.size();i++)
		{
			String url=(String)V.get(i).get(8);
			String fileDirectory=(String)V.get(i).get(7);
			String fileName=(String)V.get(i).get(1);
			long point=this.TestFile((String)V.get(i).get(1));
			
			//索引8对应xml中的url,7对应fileDirstory,1对应fileName,最后一是point
			DownLoad downLoad=new DownLoad(i,url,fileDirectory,fileName,point);
			
			//最早加载历史任务时,设置任务运行状态,只分为暂停和断开.
			downLoad.setDownLoadSign(Integer.parseInt((String)V.get(i).get(0)));
			downLoad.second=Long.parseLong((String)V.get(i).get(5));//已用时间.
			
			list.add(downLoad);//添加锁.
			HashMap<String,String> hm=new HashMap<String,String>(7);
			hm.put("正在获取链接...","");
			hm.put("文件名称",fileName);
			if("0".equals(V.get(i).get(9)))
			{
				hm.put("文件长度","未知");						//添加键值对.
			}
			else
			{
				hm.put("文件长度",(String)V.get(i).get(9));
			}
			hm.put("文件类型",(String)V.get(i).get(6));
			hm.put("保存路径",fileDirectory+fileName);
			hm.put("链接地址",url);
			hm.put("对方主机",(String)V.get(i).get(10));
			testHashMap.add(hm);
			
			timeList.add(downLoad.new CounterThread()); //添加计时线程锁.
		}
	}
	
	
	/**
	 * 构造一个空的Vector,便于构造一个空的表格.
	 * @return
	 */
	public Vector<Vector> getNullVector()
	{
		Vector<Vector> VC=new Vector<Vector>();
		Vector<String> vc=new Vector<String>();

		vc.add("");
		vc.add("");
		vc.add("");
		vc.add("");
		vc.add("");
		vc.add("");
		vc.add("");
		VC.add(vc);
		return VC;
	}
	
	
	
	
	/**
	 * 构造一张图片.
	 * @param ImageFile
	 * @return
	 */
	public ImageIcon getImageIcon(String ImageFile)
	{
		return new ImageIcon(getClass().getResource(ImageFile));
	}

	
	/**
	 * 测试给定的文件是否存在,并返回其长度,如果不存在,返回0;
	 * @param filePath
	 * @return
	 */
	public long TestFile(String filePath)
	{
		File file=new File(filePath);
		return file.exists()?file.length():0;
	}

	
}


/**
 * 
 * 继承自JTable,重写其中一个构造子.便于显示图片.由createJTable()方法调用.
 *
 */

class newJTable extends JTable
{
	private static final long serialVersionUID = -2814919364264878274L;
	public newJTable(final Object[][] rowData, final Object[] columnNames)
	{
		super(new AbstractTableModel()
		{
			private static final long serialVersionUID = 8551282884032184155L;
			public String getColumnName(int column)
			{ 
				return columnNames[column].toString(); 
			}
			public int getRowCount()
			{ 
				return rowData.length; 
			}
			public int getColumnCount() 
			{ 
				return columnNames.length;
			}
			public Object getValueAt(int row, int col)
			{
				return rowData[row][col];
			}

			//将isCellEditable的默认返回值设为false,即不可编辑.
			public boolean isCellEditable(int row, int column) 
			{ 
				//return true;
				return false;
			}
			public void setValueAt(Object value, int row, int col)
			{
				rowData[row][col] = value;
				fireTableCellUpdated(row, col);

			}
			//重写AbstractTableModel的方法,
			public Class<?> getColumnClass(int col)
			{
				//return Object.class;
				return rowData[0][col].getClass();
			}  
			 
		});
		this.setDragEnabled(false);
	}
	
}






















