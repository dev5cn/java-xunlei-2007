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
 *������ʾ��������ı��.
 *
 */
public class XDownTable
{
	private static final long serialVersionUID = 2621790669412858341L;
	private JTable downTable;
	
	/**
	 * ������.
	 */
	private static int rowIndex=0;
	

	/**
	 * ����һ�ű�,������ʾ������������״̬.
	 * @return
	 */
	public JTable xDownTable() 
	{
		//�õ���.
		downTable=new XDownTable().createJTable(this.getVector(),1);
	
		//����Ա��ı���,����������.
		downTable.getTableHeader().setReorderingAllowed(false);
		
		//�еĿ�ȿ���.//525��
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
		
		
		
		//����¼�.
		downTable.addMouseListener(new MouseAdapter()
		{
			//����˫���¼�.
			public void mouseClicked(MouseEvent e)
			{
				if(e.getButton()==3) return;//�����Ҽ�
				if(e.getClickCount()==2)//�õ����˫���¼�.
				{
					//�õ���ѡ�����.
					if(rowIndex>=SaveRunTime.getLIST().size())
					{
						System.out.println("�㵽�˿հ���.");
						return;
					}
					else
					{	//�õ��߳���.
						DownLoad downLoad=(DownLoad)SaveRunTime.getLIST().get(rowIndex);
						//����߳����ڽ���.���������ȴ�״̬.
						if(downLoad.getDownLoadSign()==1)
						{	
							//��ͣ��ť������.
							SaveRunTime.getPAUSE().setEnabled(false);
							downLoad.setDownLoadSign(0);//֪ͨ�߳���ͣ.
							//���������߳�������ͼ��.
							downTable.setValueAt(new XDownTable().getImageIcon("/image/dpause.png"), rowIndex, 0);
						}
						//���������ͣ.
						else if(downLoad.getDownLoadSign()==0)
						{	
							synchronized(downLoad)
							{
								downLoad.notify();
								SaveRunTime.getPAUSE().setEnabled(true);
								downTable.setValueAt(new XDownTable().getImageIcon("/image/ing.png"), rowIndex, 0);
								//�̱߳����Ѻ�������Լ��Ŀ��ر���.
							}
						}
						//������������һ���߳�.һ���쳣�жϵ��߳�,����һ����ʷ����.
						else if(downLoad.getDownLoadSign()==-1||downLoad.getDownLoadSign()==-3)	
						{
							//�õ��ļ�·�����ļ���.
							String saveAs=downLoad.getDownLoadFileName();
							String choos=downLoad.getDownLoadFileDirectory();
							String url=downLoad.getURL(downLoad);//�õ�����·��.
							
							//�õ��ϵ�.
							long point=new XDownTable().TestFile(choos+saveAs);
							//��������һ�������߳�.
							new Thread(new TestLink(downLoad.row,url,choos,saveAs,point)).start();
						}
						
					}
				}
			}
		
			//�����һ��¼�.ֻ���Ѿ����������������Ч.
			public void mousePressed(MouseEvent ex)
			{
				if(ex.getButton()==1)//�������.
				return;
				
				Point point=new Point(ex.getX(),ex.getY());	//��������,
				final int row=downTable.rowAtPoint(point);	//������������е�����.	
				
				
				downTable.requestFocus();//�л�ý���.
				downTable.setRowSelectionInterval(0, row);
				
				JPopupMenu jpop=new JPopupMenu();		 //׼������һ���Ҽ��˵�.
				JMenuItem start=new JMenuItem("��ʼ");
				
				JMenuItem newTask=new JMenuItem("�½�");
				newTask.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						SaveRunTime.getNEWTASK_JDIALOG().setVisible(true);
					}
				});
				
				
				JMenuItem del=new JMenuItem("ɾ��");
				
				//ɾ����ť�����¼�.
				del.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						//ɾ��һ��������Ҫ,ɾ����һ������Ӧ�������߳���,��ʱ�߳���.
						DownLoad downLoad=(DownLoad)(SaveRunTime.getLIST().get(row));
						
						//������߳���������.
						if(downLoad.getDownLoadSign()==1)
						{
							downLoad.setDownLoadSign(-2);//�����̵߳���ɾ��.
						}
						else//�̴߳���˯�߻��ж�״̬ʱ.ֱ��ɾ��.
						{
							try
							{
								downLoad.taskEnd(row);
								new XmlOperation().writerTaskInfo();//дxml.
							} catch (Exception ex)
							{
								ex.printStackTrace();
							}
						}
					}
				});
				
				
				//��ʼ��ť�����¼�.(δ���)
				start.addActionListener(new ActionListener()
				{
					//�ж��̵߳�����״̬.
					public void actionPerformed(ActionEvent e)
					{	//�õ������е��̶߳���.
						DownLoad downLoad=(DownLoad)(SaveRunTime.getLIST().get(row));
						int sign=downLoad.getDownLoadSign();//�߳�״̬.
						if(sign==-1)//�߳��쳣��ֹ״̬.��������һ���߳�.
						{
							//�õ��ļ�·��
							String saveAs=downLoad.getDownLoadFileName();
							String choos=downLoad.getDownLoadFileDirectory();
							String url=downLoad.getURL(downLoad);//�õ�����·��.
							//�õ��ϵ�.
							long point=new XDownTable().TestFile(choos+saveAs);
							
							//��������һ�������߳�.
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
											//�˵����������.
				jpop.setLocation(ex.getX()+166,ex.getY()+180);
				jpop.setVisible(true);
				
			}	
		});
		
		
		
		
		
		
		//������,��ѡ����ʱ����.������ʾ������Ϣ.
		DefaultListSelectionModel  listSM=(DefaultListSelectionModel)downTable.getSelectionModel();
		listSM.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		listSM.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				//Դ.
				ListSelectionModel lm=(ListSelectionModel)e.getSource();
				
				if(e.getValueIsAdjusting())
				{
					//����һ����Ӧ.
					return;
				}
				//�õ���ѡ�����.
				rowIndex=lm.getLeadSelectionIndex();
				rowIndex=lm.getLeadSelectionIndex();
				if(rowIndex>SaveRunTime.getSAVE_HASHMAP().size()-1)
				{//���ѡ���հ���,ֱ�ӷ���.
					return;
				}
				
				HashMap hm=SaveRunTime.getSAVE_HASHMAP().get(rowIndex);
				JTable xdit=SaveRunTime.getDOWN_INFOTABLE();
				xdit.setValueAt("���ڻ�ȡ����...",0,0);	
				xdit.setValueAt("",0,1);
				xdit.setValueAt("�ļ�����",1,0);
				xdit.setValueAt(hm.get("�ļ�����"),1,1);
				xdit.setValueAt("�ļ�����",2,0);
				xdit.setValueAt(hm.get("�ļ�����"),2,1);
				xdit.setValueAt("�ļ�����",3,0);
				xdit.setValueAt(hm.get("�ļ�����"),3,1);
				xdit.setValueAt("����·��",4,0);
				xdit.setValueAt(hm.get("����·��"),4,1);
				xdit.setValueAt("���ӵ�ַ",5,0);
				xdit.setValueAt(hm.get("���ӵ�ַ"),5,1);
				xdit.setValueAt("�Է�����",6,0);
				xdit.setValueAt(hm.get("�Է�����"),6,1);
				return;
			}
		});
		
		 //������ɫ.
		downTable.setGridColor(new Color(226, 238, 249));
		
		//��ͷ.
		JTableHeader downTableHeader= downTable.getTableHeader();
		
		//��ͷ���±߿�
		downTableHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.gray));
		
		//���쵥Ԫ�����Ⱦ��.
		DefaultTableCellRenderer dtc=new DefaultTableCellRenderer();
		
		//���ñ���ɫ����������ɫһ��.
		dtc.setBackground(new Color(226, 238, 249));
		//����ͷ���óɴ���Ⱦ��.
		downTableHeader.setDefaultRenderer(dtc);
		//�������.
		SaveRunTime.setDOWN_TABLE(downTable);
		return downTable;
	}
	
	
	/**
	 * ����һ��������ɵ�JScrollPane
	 * @param table
	 * @return
	 */
	public JScrollPane getJScrollPane(JTable table)
	{
		JScrollPane jScrollTable = new JScrollPane();
		//���������Ļ�������
		jScrollTable.setBackground(new java.awt.Color(255, 255, 255));
        jScrollTable.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollTable.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        //������û�б߿�.
        jScrollTable.setBorder(BorderFactory.createEmptyBorder());
		//������ڵĹ�����������ɫ.
		jScrollTable.setBackground(new Color(226, 238, 249));
		//������.
		jScrollTable.setViewportView(table);
		
		//�׳���������.
		return jScrollTable;
	}
		
	
	/**
	 * ������������Ϣ��.
	 * @return
	 */
	public JTable taskComplete()
	{
		//�յı�.
		JTable downLoadInfo=new XDownTable().createJTable(this.getNullVector(),0);
	
		//����Ա��ı���,����������.
		downLoadInfo.getTableHeader().setReorderingAllowed(false);
		TableColumnModel tc=downLoadInfo.getColumnModel();
		
		//�еĿ�ȿ���.//525��
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
		
		//��ͷ.
		JTableHeader downTableHeader= downLoadInfo.getTableHeader();
		
		//��ͷ���±߿�
		downTableHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.gray));
		
		//���쵥Ԫ�����Ⱦ��.
		DefaultTableCellRenderer dtc=new DefaultTableCellRenderer();
		
		//���ñ���ɫ����������ɫһ��.
		dtc.setBackground(new Color(226, 238, 249));
		//����ͷ���óɴ���Ⱦ��.
		downTableHeader.setDefaultRenderer(dtc);
		
		//�������.
		SaveRunTime.setDOWN_INFOTABLE(downLoadInfo);
		return downLoadInfo;
	}
	
	
	
	/**
	 * ����һ�ű� 18.7 typeֵΪ0����1,Ϊ0ʱ����һ���յ�Vector.
	 */
	@SuppressWarnings("unchecked")
	public JTable createJTable(Vector<Vector> vc,int type)
	{
		Object[][]obj=new Object[18][7];
		String [] str1= new String []{"״̬", "�ļ�����", "����", "�ٶ�","ʣ��ʱ��", "��ʱ", "�ļ�����",};
		
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
				String str="";//�������null
				if("".equals(vcc.get(5))==false)
				{//����""����ʱ�׳��쳣.
					str=new DownLoad().new CounterThread().getTime(Long.parseLong((String)vcc.get(5)));
				}
				obj[i][0]=vcc.get(0);
				obj[i][1]=vcc.get(1);
				obj[i][2]=vcc.get(2);
				obj[i][3]=vcc.get(3);
				obj[i][4]=vcc.get(4);
				obj[i][5]=str;
				obj[i][6]=vcc.get(6);	//���ֻ�õ�Vector��ǰ�߸�ֵ.
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
	 * ���ش�xml�ļ��еõ���Vector,���Ϊ��,�򷵻�һ���� "" ��Vector
	 * ������������ʱ��getJTable()����.
	 * @return
	 */
	public Vector<Vector> getVector()
	{
		try
		{	//��ȡxml�ļ�.
			Vector<Vector> V=new XmlOperation().readTaskInfo();
			if(V.size()!=0)
			{
				SaveRunTime.getNEWTASK().setRow(V.size());//������ֵ������ʷ��¼����.
				SaveRunTime.setXML_VC(V);
				this.loadHistoryTask(V); //������ʷ��¼.
				return V;
			}
			else//����û����ʷ��¼.
			{
				return this.getNullVector();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			//��xml�ļ��õ��쳣ʱ,Ҳ����һ���յ�Vector
			return this.getNullVector();
		}
	}
	
	
	
	/**
	 * ������ʷ��¼.������������ʱ��getVecotr()����.
	 * @param V
	 */
	private void loadHistoryTask(Vector<Vector> V)
	{
		
		//������ʷ����,��Ϊ�������������.
		List<Object> list=SaveRunTime.getLIST();
		List<Object> timeList=SaveRunTime.getTIMELIST();
		List<HashMap<String,String>> testHashMap=SaveRunTime.getSAVE_HASHMAP();
		for(int i=0;i<V.size();i++)
		{
			String url=(String)V.get(i).get(8);
			String fileDirectory=(String)V.get(i).get(7);
			String fileName=(String)V.get(i).get(1);
			long point=this.TestFile((String)V.get(i).get(1));
			
			//����8��Ӧxml�е�url,7��ӦfileDirstory,1��ӦfileName,���һ��point
			DownLoad downLoad=new DownLoad(i,url,fileDirectory,fileName,point);
			
			//���������ʷ����ʱ,������������״̬,ֻ��Ϊ��ͣ�ͶϿ�.
			downLoad.setDownLoadSign(Integer.parseInt((String)V.get(i).get(0)));
			downLoad.second=Long.parseLong((String)V.get(i).get(5));//����ʱ��.
			
			list.add(downLoad);//�����.
			HashMap<String,String> hm=new HashMap<String,String>(7);
			hm.put("���ڻ�ȡ����...","");
			hm.put("�ļ�����",fileName);
			if("0".equals(V.get(i).get(9)))
			{
				hm.put("�ļ�����","δ֪");						//��Ӽ�ֵ��.
			}
			else
			{
				hm.put("�ļ�����",(String)V.get(i).get(9));
			}
			hm.put("�ļ�����",(String)V.get(i).get(6));
			hm.put("����·��",fileDirectory+fileName);
			hm.put("���ӵ�ַ",url);
			hm.put("�Է�����",(String)V.get(i).get(10));
			testHashMap.add(hm);
			
			timeList.add(downLoad.new CounterThread()); //��Ӽ�ʱ�߳���.
		}
	}
	
	
	/**
	 * ����һ���յ�Vector,���ڹ���һ���յı��.
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
	 * ����һ��ͼƬ.
	 * @param ImageFile
	 * @return
	 */
	public ImageIcon getImageIcon(String ImageFile)
	{
		return new ImageIcon(getClass().getResource(ImageFile));
	}

	
	/**
	 * ���Ը������ļ��Ƿ����,�������䳤��,���������,����0;
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
 * �̳���JTable,��д����һ��������.������ʾͼƬ.��createJTable()��������.
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

			//��isCellEditable��Ĭ�Ϸ���ֵ��Ϊfalse,�����ɱ༭.
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
			//��дAbstractTableModel�ķ���,
			public Class<?> getColumnClass(int col)
			{
				//return Object.class;
				return rowData[0][col].getClass();
			}  
			 
		});
		this.setDragEnabled(false);
	}
	
}






















