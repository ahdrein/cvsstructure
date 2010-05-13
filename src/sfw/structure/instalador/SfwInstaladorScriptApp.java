/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sfw.structure.instalador;

import java.awt.Window;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

public class SfwInstaladorScriptApp extends SingleFrameApplication
{
  protected void startup()
  {
    //show(new SfwInstaladorScriptView(this));
  }

  protected void configureWindow(Window root)
  {
  }

  public static SfwInstaladorScriptApp getApplication()
  {
    return ((SfwInstaladorScriptApp)Application.getInstance(SfwInstaladorScriptApp.class));
  }

  public static void main(String[] args)
  {
    launch(SfwInstaladorScriptApp.class, args);
  }
}