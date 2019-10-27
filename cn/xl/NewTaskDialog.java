package cn.xl;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;


/**
 * �����ݾֳ�ʼ��ʱ,���౻���ؼ���.
 *
 */

public class NewTaskDialog
{
	private JButton buttBrowse = new JButton();
	private JButton buttSure = new JButton();
	private JButton  buttCancel = new JButton();

	private JComboBox jCoChoo = new JComboBox();
	private JLabel jLabel1 = new JLabel();
	private JLabel jLabel2 = new JLabel();
	private JLabel jLabel3 = new JLabel();
	private JLabel jLabel4 = new JLabel();
	private JPanel  jdp = new JPanel();
	private JTextField jTurl = new JTextField();
	
	private JTextField jTSaveas = new JTextField();
	private JLabel jdialogLogo = new JLabel();
	
	private JDialog newTaskDialog=null;
	private int row=0;
	
	/**
	 * ����һ��JDialog.
	 * @param xl
	 */
	NewTaskDialog(JFrame xl)
	{
		newTaskDialog = new JDialog(xl,"�����µ���������",true);
	}
	
	
	/**
	 * �����������.
	 *
	 */
	
	public void initComponents()
	{
	        newTaskDialog.getContentPane().setLayout(null);
	        newTaskDialog.setBackground(new Color(226, 238, 249));
	        newTaskDialog.setSize(430, 240);
	        newTaskDialog.setLocation(150, 150);
	        
	        
	        jdp.setLayout(null);
	        jdp.setBackground(new Color(226, 238, 249));
	        jdialogLogo.setIcon(new ImageIcon(getClass().getResource("/image/Digloglogo.png")));
	        jdp.add(jdialogLogo);
	        jdialogLogo.setBounds(25, 0, 373, 50);
	        LimitedDocument ld=new LimitedDocument(120);
	        jTurl.setDocument(ld);
	        
	        //�ı��ֶ��¼�,���ݷ��仯ʱ��Ӧ.
	        jTurl.getDocument().addDocumentListener(new DocumentListener()
	        {

				public void changedUpdate(DocumentEvent e)
				{
					
				}
				public void insertUpdate(DocumentEvent e)
				{
					String str=jTurl.getText();
					str=new DownLoad().interceptURL(str);
					jTSaveas.setText(str);
				}
				public void removeUpdate(DocumentEvent e)
				{
					String str=jTurl.getText();
					str=new DownLoad().interceptURL(str);
					jTSaveas.setText(str);
				}
	        	
	        });
	      
	        
	        jdp.add(jTurl);
	        jTurl.setBounds(90, 70, 320, 20);
	        jLabel1.setText("\u7f51\u5740:(URL)");
	        jdp.add(jLabel1);
	        jLabel1.setBounds(20, 70, 60, 20);
	        jLabel2.setText("\u5b58\u50a8\u76ee\u5f55:");
	        jdp.add(jLabel2);
	        jLabel2.setBounds(20, 110, 60, 20);
	        jLabel3.setText("\u53e6\u5b58\u540d\u79f0:");
	        jdp.add(jLabel3);
	        jLabel3.setBounds(20, 140, 54, 20);
	        
	        
	        newTaskDialog.getContentPane().add(jdp);
	        jdp.setBounds(0, 0, 430, 210);
	        
	        //�յ�.����.
	        jLabel4.setForeground(Color.blue);
	        jLabel4.setBounds(60, 170, 100, 20);
	        jdp.add(jLabel4);
	        
	        //�����ť
	        buttBro();
	        //ѡ��·��.
	        jCoCh();
	        //���Ϊ
	        jTSave();
	        //ȷ��
	        buttSur();
	        //ȡ��
	        buttCan();
	      
	       newTaskDialog.setVisible(false);
	       SaveRunTime.setNEWTASK_JDIALOG(newTaskDialog);
	    }                     
	
	/**
	 * ȡ����ť.
	 *
	 */
	
	private void buttCan()
	{
		buttCancel.setText("\u53d6\u6d88");
		buttCancel.setMargin(new java.awt.Insets(0, 0, 0, 0));
		jdp.add(buttCancel);
		buttCancel.setBounds(353, 170, 50, 22);
		buttCancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				newTaskDialog.dispose();
				jTurl.setText("");		//�رմ���,����ı��ֶ�.
				jLabel4.setText("");
				jTSaveas.setText("");
			}
		});
	}
	
	
	/**
	 * 
	 *ȷ����ť
	 */
	
	private void buttSur()
	{
		buttSure.setText("\u786e\u5b9a");
		buttSure.setMargin(new java.awt.Insets(0, 0, 0, 0));
		jdp.add(buttSure);
		buttSure.setBounds(270, 170, 50, 22);
		
		//�����¼�.
		buttSure.addActionListener(new ActionListener()
		{
			String url=null;
			String choos=null;
			String saveAs=null;
			public void actionPerformed(ActionEvent e)
			{
				//�õ����ӵ�ַ.
				 url=jTurl.getText();
				
				//�õ�ѡ���·��.
				choos=(String)jCoChoo.getSelectedItem();
				//�õ�����ļ���.
				saveAs=jTSaveas.getText();
				
				//���ӵ�ַ��Ӧ��Ϊ��.
				if("".equals(url))
				{
					jLabel4.setText("��Ч�����ӵ�ַ");
				}
				else if("".equals(saveAs))
				{
					jLabel4.setText("��Ч�ļ���");
				}
				else if("".equals(choos))
				{
					//�������жϴ���·���Ƿ���ȷ..?
					jLabel4.setText("��Ч�Ĵ���·��");
				}
				else
				{
					//���������.//http://www.maxprocom.net/music/yinxingde.mp3
					newTaskDialog.setVisible(false);//���类����.
					
					//�õ�����JDialog����Ϣʱ,���������߳�,
					new Thread(new TestLink(row,url,choos,saveAs,0)).start();
					SaveRunTime.getNEWTASK().row++;//���������Ƿ���Ч,��������1.
					
					jTurl.setText("");
					jLabel4.setText("");//�ı��ֶ�Ϊ��;
					jTSaveas.setText("");
				}
			}
		});
	}
	
	
	/**
	 * ���Ϊ.
	 *
	 */
	
	private void jTSave()
	{
		jdp.add(jTSaveas);
		jTSaveas.setBounds(90, 140, 240, 22);
	}
	
	/**
	 * 
	 *����·��.
	 */
	
	private void jCoCh()
	{
		jCoChoo.setModel(new DefaultComboBoxModel(new String[] { "C:\\", "e:\\"}));
		jCoChoo.setEditable(true);
		jdp.add(jCoChoo);
		jCoChoo.setBounds(90, 110, 240, 22);
	}
	
	
	/**
	 *���. 
	 *
	 */
	
	//���.
	private void buttBro()
	{
		buttBrowse.setText("\u6d4f\u89c8");
		buttBrowse.setBounds(353, 110, 50, 22);
		buttBrowse.setMargin(new java.awt.Insets(0, 0, 0, 0));
		jdp.add(buttBrowse);
		
		buttBrowse.addActionListener(new ActionListener()
		{
			
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser jfc=new JFileChooser(FileSystemView.getFileSystemView());
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				//���Ե����.
				jfc.setControlButtonsAreShown(true);
				//�õ��ļ�ѡ�����û�Ĭ�ϵ�·��.
				jfc.setSelectedFile(jfc.getFileSystemView().getDefaultDirectory());
				jfc.setDialogType(JFileChooser.OPEN_DIALOG);
				//ȡ��Ϊ1,ȷ��Ϊ0.
				if(jfc.showOpenDialog(newTaskDialog)==0)
				{	//���õ�JTextField
					jCoChoo.setSelectedItem(jfc.getSelectedFile().toString()+"\\");
				}
			}
		});
	}
	
	
	/**
	 * ����������.
	 * @param row
	 */
	
	//������.
	public void setRow(int row)
	{
		this.row=row;
	}
	
	/**
	 * ����������.
	 * @return
	 */
	
	public int getRow()
	{
		return this.row;
	}

	
	/**
	 * �����ı��ֶ�.
	 * 
	 */
	 class LimitedDocument extends PlainDocument
	 {   
		private static final long serialVersionUID = -4023070482794305584L;
		private int _maxLength=-1;   
		private String _allowCharAsString=null;   
		public   LimitedDocument()
		{   
			super();   
		}   
		public   LimitedDocument(int maxLength)
		{   
			super();   
			this._maxLength =maxLength;   
		}   

		public void insertString(int offset, String str, AttributeSet attrSet)
				throws BadLocationException
		{

			if (str == null)
			{
				return;
			}

			if (_allowCharAsString != null && str.length() == 1)
			{
				if (_allowCharAsString.indexOf(str) == -1)
				{
					return;
				}
			}
			char[] charVal = str.toCharArray();
			String strOldValue = getText(0, getLength());
			byte[] tmp = strOldValue.getBytes();
			if (_maxLength != -1 && (tmp.length + charVal.length > _maxLength))
			{
				return;
			}
			super.insertString(offset, str, attrSet);
		}
		public void setAllowChar(String str)
		{
			_allowCharAsString = str;
		}
	}

}
