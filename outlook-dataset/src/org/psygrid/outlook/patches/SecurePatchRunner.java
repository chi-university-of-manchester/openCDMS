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

package org.psygrid.outlook.patches;

import java.util.Properties;

import org.apache.axis.utils.Options;
import org.opensaml.SAMLAssertion;
import org.psygrid.security.authentication.client.LoginClient;
import org.psygrid.security.utils.PropertyUtilities;

/**
 * Class to apply one or more patches to the Outlook dataset,
 * where the repository is secured.
 * <p>
 * This class only deals with the security aspects (i.e. retrieving
 * a SAML assertion etc.). Everything else is delegated to PatchRunner.
 * <p>
 * The arguments should be
 * <br>
 * -l &lt;authentication-url&gt; -u &lt;username&gt; -w &lt;password&gt; &lt;project-code&gt; &lt;version-package&gt; &lt;first-patch&gt; &lt;last-patch&gt;
 *
 * @author Rob Harper
 *
 */
public class SecurePatchRunner {

    public static final String DS_NAMESPACE = "http://www.w3.org/2000/09/xmldsig#";

    private static UpdaterAndPatcherUtils utils = new UpdaterAndPatcherUtils();

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        System.setProperty("axis.socketSecureFactory",
        "org.psygrid.security.components.net.PsyGridClientSocketFactory");
        Options opts = new Options(args);
        String[] patcherArgs = opts.getRemainingArgs();
        Properties properties = PropertyUtilities.getProperties("test.properties");
        System.out.println(properties.getProperty("org.psygrid.security.authentication.client.trustStoreLocation"));
        LoginClient tc = null;

        try {
            tc = new LoginClient("test.properties");
        } catch (Exception e) {
            e.printStackTrace();
        }
        char[] password = opts.getPassword().toCharArray();
        short[] pwd = utils.getPasswordAsShort(password);

        utils.login(properties, tc, opts, pwd);

        SAMLAssertion sa = utils.getSAML(properties, password);

        PatchRunner patcher = new PatchRunner();
        patcher.patch(patcherArgs, properties.getProperty("org.psygrid.data.client.serviceURL"), sa.toString());

    }

}
