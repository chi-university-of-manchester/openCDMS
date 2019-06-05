/*
Copyright (c) 2006-2008, The University of Manchester, UK.

This file is part of PsyGrid.

PsyGrid is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as 
published by the Free Software Foundation, either version 3 of 
the License, or (at your option) any later version.

PsyGrid is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public 
License along with PsyGrid.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.psygrid.data.reporting.renderer;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.Date;

import javax.mail.internet.MimeMessage;

import org.psygrid.data.reporting.Report;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import junit.framework.TestCase;

public class ExcelRendererTest extends TestCase {

    public void testRender(){
        try{
            
            Report r = RendererTestHelper.createTestReport();
            
            FileOutputStream fos = new FileOutputStream("test.xls");
            
            ExcelRenderer renderer = new ExcelRenderer();
            renderer.render(r, fos);
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex);
        }
    }
    
    public void testRenderAndEmail(){
        try{
            Report r = RendererTestHelper.createTestReport();
            
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            
            ExcelRenderer renderer = new ExcelRenderer();
            renderer.render(r, os);
            
            JavaMailSenderImpl sender = new JavaMailSenderImpl();
            sender.setHost("echobase.smb.man.ac.uk");
            
            MimeMessage message = sender.createMimeMessage();

            //use the true flag to indicate you need a multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo("lucy.bridges@manchester.ac.uk");
            helper.setFrom("lucy.bridges@manchester.ac.uk");
            helper.setSubject("Test with PDF");
            helper.setSentDate(new Date());

            //use the true flag to indicate the text included is HTML
            helper.setText(
                    "<html><body><p>Test email with a spreadsheet (hopefully!)</p></body></html>", true);

            InputStreamSource src = new ByteArrayResource(os.toByteArray());
            helper.addAttachment("test.xls", src);

            sender.send(message);
                                
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex);
        }
    }
    
}
