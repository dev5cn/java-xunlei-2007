package cn.xl;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

/**
 * 顶部菜单
 *
 */

public class XJMenuBar
{
	
	
	private JMenu File=new JMenu();;
 	private JMenu edit= new JMenu();
    private JMenu help= new JMenu();
    private JMenu leiqu= new JMenu();
    private JMenu module= new JMenu();
    private JMenu see= new JMenu();
    private JMenu setup= new JMenu();
    private JMenu tools= new JMenu();
    private JMenuBar jMenuBar1=new JMenuBar();
   
    /**
     * 初始化JMenuBar.
     * @return
     */
    public JMenuBar xJMenuBar()
    {
    	jMenuBar1.setBackground(new java.awt.Color(226, 238, 249));
        jMenuBar1.setBorderPainted(false);
        File.setBackground(new java.awt.Color(226, 238, 249));
        File.setText("\u6587\u4ef6(F)");
        jMenuBar1.add(File);

        edit.setBackground(new java.awt.Color(226, 238, 249));
        edit.setText("\u7f16\u8f91(E)");
        jMenuBar1.add(edit);

        see.setBackground(new java.awt.Color(226, 238, 249));
        see.setText("\u67e5\u770b(F)");
        jMenuBar1.add(see);

        setup.setBackground(new java.awt.Color(226, 238, 249));
        setup.setText("\u5e38\u7528\u8bbe\u7f6e(R)");
        jMenuBar1.add(setup);

        leiqu.setBackground(new java.awt.Color(226, 238, 249));
        leiqu.setText("\u96f7\u533a(C)");
        jMenuBar1.add(leiqu);

        tools.setBackground(new java.awt.Color(226, 238, 249));
        tools.setText("\u5de5\u5177(T)");
        jMenuBar1.add(tools);

        module.setBackground(new java.awt.Color(226, 238, 249));
        module.setText("\u7ec4\u4ef6(P)");
        jMenuBar1.add(module);

        help.setBackground(new java.awt.Color(226, 238, 249));
        help.setText("\u5e2e\u52a9(H)");
        jMenuBar1.add(help);

        return jMenuBar1;
    }

}
