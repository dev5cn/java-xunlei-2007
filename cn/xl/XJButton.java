package cn.xl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;



/**
 * �ռ������еİ�ť(δ���)
 */

public class XJButton
{
	
	private static final long serialVersionUID = 3930136142927257419L;
	
	private JButton bbs= new JButton();
    private JButton delete= new JButton();
    private JButton leixun= new JButton();
    private JButton newTask= new JButton();
    private JButton startTask= new JButton();
    private JButton open= new JButton();
    private JButton pause= new JButton();
    private JButton scoure= new JButton();
  
    
    /**
     * ������ť
     */
	private void newTaskButton(JPanel jp)
	{
		newTask.setBackground(new java.awt.Color(226, 238, 249));
        newTask.setIcon(new ImageIcon(getClass().getResource("/image/newtask.png")));
        newTask.setBorder(BorderFactory.createEtchedBorder());
        newTask.setBorderPainted(false);
        newTask.setFocusPainted(false);
        
       
        //��������,����뿪����ʱ����.������ʾ���.
        newTask.addMouseListener(new MouseAdapter()
        {
            public void mouseExited(MouseEvent evt)
            {
            	newTask.setBorder(BorderFactory.createEtchedBorder());
            }
            public void mouseEntered(MouseEvent e)
        	{
            	newTask.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        	}
        });
       
        
        newTask.addActionListener(new ActionListener()
        {
			public void actionPerformed(ActionEvent e)
			{
				SaveRunTime.getNEWTASK_JDIALOG().setVisible(true);
			}
        });
        jp.add(newTask);
        newTask.setBounds(50, 60, 34, 49);
	}

	
	/**
	 * ��ʼ��ť
	 * @param jp
	 */
	
	private void startTaskButton(JPanel jp)
	{
		startTask.setBackground(new java.awt.Color(226, 238, 249));
        startTask.setIcon(new ImageIcon(getClass().getResource("/image/startn.png")));
        startTask.setBorder(BorderFactory.createEtchedBorder());
        startTask.setBorderPainted(false);
        startTask.setFocusPainted(false);
        startTask.addMouseListener(new MouseAdapter()
        {
        	
        	public void mouseExited(MouseEvent e) 
        	{
        		startTask.setBorder(BorderFactory.createEtchedBorder());
        	}
        	
        	public void mouseEntered(MouseEvent e)
        	{
        		startTask.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        	}

        });
        jp.add(startTask);
        startTask.setBounds(10, 60, 36, 48);
	}
    
    
	
    /**
     * ����ȫ����ť.
     * @param jp
     */
    
    public void xJButton(JPanel jp)
    {
    	startTaskButton(jp);
        newTaskButton(jp);
        pauseButton(jp);
        deleteButton(jp);
        scoureButton(jp);
        leixunButton(jp);
        bbsButton(jp);
        openButton(jp);

    }

    /**
     * �򿪰�ť.
     * @param jp
     */
	private void openButton(JPanel jp)
	{
		open.setBackground(new java.awt.Color(226, 238, 249));
        open.setIcon(new ImageIcon(getClass().getResource("/image/open.png")));
        open.setBorder(BorderFactory.createEtchedBorder());
        open.setBorderPainted(false);
        open.setFocusPainted(false);
        open.setFocusable(false);
        open.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jp.add(open);
        open.setBounds(170, 60, 33, 47);
        
        //����뿪����ʱ����.
        open.addMouseListener(new MouseAdapter()
        {
        	
        	public void mouseExited(MouseEvent e) 
        	{
        		open.setBorder(BorderFactory.createEtchedBorder());
        	}
        	
        	public void mouseEntered(MouseEvent e)
        	{
        		open.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        	}

        });
	}

	/**
	 * ��̳��ť.
	 * @param jp
	 */
	
	private void bbsButton(JPanel jp)
	{
		bbs.setBackground(new java.awt.Color(226, 238, 249));
        bbs.setIcon(new ImageIcon(getClass().getResource("/image/bbs.png")));
        bbs.setBorder(BorderFactory.createEtchedBorder());
        bbs.setBorderPainted(false);
        bbs.setFocusable(false);
        bbs.addMouseListener(new MouseAdapter()
        {
        	
        	public void mouseExited(MouseEvent e) 
        	{
        		bbs.setBorder(BorderFactory.createEtchedBorder());
        	}
        	
        	public void mouseEntered(MouseEvent e)
        	{
        		bbs.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        	}

        });
        jp.add(bbs);
        bbs.setBounds(290, 60, 33, 49);
	}

	/**
	 * ��Ѷ.
	 * @param jp
	 */
	private void leixunButton(JPanel jp)
	{
		leixun.setBackground(new java.awt.Color(226, 238, 249));
        leixun.setIcon(new ImageIcon(getClass().getResource("/image/leixun.png")));
        leixun.setBorder(BorderFactory.createEtchedBorder());
        leixun.setFocusable(false);
        leixun.setBorderPainted(false);
        
        leixun.addMouseListener(new MouseAdapter()
        {
        	
        	public void mouseExited(MouseEvent e) 
        	{
        		leixun.setBorder(BorderFactory.createEtchedBorder());
        	}
        	
        	public void mouseEntered(MouseEvent e)
        	{
        		leixun.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        	}

        });
        jp.add(leixun);
        leixun.setBounds(250, 60, 33, 48);
	}

	/**
	 * ��Դ
	 * @param jp
	 */
	private void scoureButton(JPanel jp)
	{
		scoure.setBackground(new java.awt.Color(226, 238, 249));
        scoure.setIcon(new ImageIcon(getClass().getResource("/image/scoure.png")));
        scoure.setBorder(BorderFactory.createEtchedBorder());
        scoure.setBorderPainted(false);
        scoure.setFocusable(false);
        scoure.addMouseListener(new MouseAdapter()
        {
        	
        	public void mouseExited(MouseEvent e) 
        	{
        		scoure.setBorder(BorderFactory.createEtchedBorder());
        	}
        	
        	public void mouseEntered(MouseEvent e)
        	{
        		scoure.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        	}

        });
        jp.add(scoure);
        scoure.setBounds(210, 60, 33, 51);
	}

	/**
	 * ɾ��.
	 * @param jp
	 */
	private void deleteButton(JPanel jp)
	{
		delete.setBackground(new java.awt.Color(226, 238, 249));
        delete.setIcon(new ImageIcon(getClass().getResource("/image/delete1.png")));
        delete.setBorder(BorderFactory.createEtchedBorder());
        delete.setBorderPainted(false);
        delete.setFocusable(false);
        delete.addMouseListener(new MouseAdapter()
        {
        	
        	public void mouseExited(MouseEvent e) 
        	{
        		delete.setBorder(BorderFactory.createEtchedBorder());
        	}
        	
        	public void mouseEntered(MouseEvent e)
        	{
        		delete.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        	}

        });
        jp.add(delete);
        delete.setBounds(130, 60, 30, 47);
	}

	/**
	 * ��ͣ.
	 * @param jp
	 */
	private void pauseButton(JPanel jp)
	{
		pause.setBackground(new java.awt.Color(226, 238, 249));
        pause.setIcon(new ImageIcon(getClass().getResource("/image/pause.png")));
        pause.setBorder(BorderFactory.createEtchedBorder());
        pause.setBorderPainted(false);
        pause.setFocusable(false);
        
        
        //�����¼������߳���ͣ����.
        pause.addActionListener(new ActionListener()
        {
			public  void actionPerformed(ActionEvent e)
			{	
				pause.setEnabled(false);
			}
        	
        });
        
        
        pause.addMouseListener(new MouseAdapter()
        {
        	
        	public void mouseExited(MouseEvent e) 
        	{
        		pause.setBorder(BorderFactory.createEtchedBorder());
        	}
        	
        	public void mouseEntered(MouseEvent e)
        	{
        		pause.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        	}

        });
        
        jp.add(pause);
        pause.setBounds(90, 60, 33, 47);
        SaveRunTime.setPAUSE(pause);
	}
	
	/**
	 * ��ͣ.
	 * @param p
	 */
	public void setPause(boolean p)
	{
		pause.setEnabled(p);
	}
}
