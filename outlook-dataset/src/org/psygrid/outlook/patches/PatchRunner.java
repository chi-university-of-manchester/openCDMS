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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.repository.client.RepositoryClient;

/**
 * Class to apply one or more patches to the Outlook dataset.
 * <p>
 * The idea of patching the dataset is to apply changes and fixes
 * to an already deployed dataset, so as to make it identical
 * to a dataset created from the HEAD revision of the trunk.
 * <p>
 * Patch classes must implement the IPatch interface, and should
 * be called Patch1, Patch2, ..., PatchN. These patches are then
 * applied in sequence.
 * <p>
 * The arguments should be
 * <br>
 * &lt;project-code&gt; &lt;version-package&gt; &lt;first-patch&gt; &lt;last-patch&gt;
 *
 * @author Rob Harper
 *
 */
public class PatchRunner {

    private static final String VERSION = "0.9.3 Patch";

    /**
     * @param args
     */
    public static void main(String[] args) {
        try{
            PatchRunner patcher = new PatchRunner();
            patcher.patch(args, null, null);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * Patch the dataset by applying the patches defined in the
     * arguments.
     *
     * @param args Command line arguments. There should be two arguments,
     * both integers, which are respectively the number of the first and
     * last patches to apply.
     * @param repositoryUrl The URL of the data repository to retrieve
     * the dataset to be patched. If <code>null</code>, the default URL
     * in the RepositoryClient is used.
     * @param saml SAML assertion for security system.
     * @throws Exception
     */
    public void patch(String[] args, String repositoryUrl, String saml) throws Exception {

        if ( 4 != args.length ){
            System.out.println("Usage: PatchRunner <project-code> <version-package> <first-patch> <last-patch>");
            return;
        }
        String projectCode = args[0];
        String pkg = args[1];
        int firstPatch = Integer.parseInt(args[2]);
        int lastPatch = Integer.parseInt(args[3]);
        if ( lastPatch < firstPatch ){
            System.out.println("Last patch must be greater than or equal to first patch");
            return;
        }

        //user confirmation
        System.out.println("About to apply the following patches to the "+projectCode+" dataset:");
        List<AbstractPatch> patches = new ArrayList<AbstractPatch>();
        for ( int i=firstPatch; i<=lastPatch; i++ ){
            AbstractPatch patch = (AbstractPatch)Class.forName(pkg+".Patch"+i).newInstance();
            patches.add(patch);
            System.out.println("  Patch"+i+" ("+patch.getName()+")");
        }
        System.out.println("Please confirm (type 'yes' to confirm):");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String str = in.readLine();
        if ( str.equals("yes") ){
            System.out.println("Patching...");
        }
        else{
            System.out.println("Aborting.");
            return;
        }

        UpdaterAndPatcherUtils utils = new UpdaterAndPatcherUtils();
        RepositoryClient client = utils.getRepositoryClient(repositoryUrl);
        DataSet ds = utils.loadDataset(client, projectCode, saml);

        //check the current patch level of the dataset
        int currentPatch = 0;
        String currentVersion = ds.getVersionNo();
        if ( null != currentVersion && currentVersion.contains("Patch")){
            currentPatch = Integer.parseInt(currentVersion.substring(currentVersion.indexOf("Patch")+5));
        }

        System.out.println("Current patch level of dataset is "+currentPatch);
        if ( firstPatch <= currentPatch ){
            System.out.println("The first patch specified has already been applied: current patch="+currentPatch+", first patch="+firstPatch);
            System.out.println("Aborting.");
            return;
        }
        else if ( firstPatch > currentPatch+1 ){
            System.out.println("First patch to apply is not one greater than the current patch.");
            System.out.println("Patches are intended to be applied sequentially. Continue? (type 'yes' to continue)");
            if ( !in.readLine().equals("yes") ){
                System.out.println("Aborting.");
                return;
            }
        }

        //apply the patches
        int counter = firstPatch;
        boolean dsSaved = false;
        for ( AbstractPatch patch: patches ){
            if ( patch.isolated() && counter > firstPatch ){
                //isolated patch - save dataset then reload it
                System.out.println("Saving dataset after applying patches prior to "+patch.getClass().getSimpleName());
                setVersion(ds, counter-1);
                client.patchDataSet(ds, saml);
                System.out.println("Reloading dataset...");
                ds = utils.loadDataset(client, projectCode, saml);
            }
            Object data = patch.preApplyPatch(ds, client, saml);
            patch.applyPatch(ds, saml);
            if ( patch.isolated() ){
                //isolated patch - save dataset then reload it
                System.out.println("Saving dataset after applying patch "+patch.getClass().getSimpleName());
                setVersion(ds, counter);
                client.patchDataSet(ds, saml);
                ds = utils.loadDataset(client, projectCode, saml);
                dsSaved = true;
            }
            else{
                dsSaved = false;
            }
            patch.postApplyPatch(ds, data, client, saml);
            System.out.println("Applied "+patch.getClass().getSimpleName()+" ("+patch.getName()+")");
            counter++;
        }

        //save the dataset
        if ( !dsSaved ){
            System.out.println("Saving the dataset...");
            setVersion(ds, lastPatch);
            client.patchDataSet(ds, saml);
        }

        System.out.println("Done. Current patch level of the dataset is "+lastPatch);
    }

    private void setVersion(DataSet ds, int lastPatch){
        String currentVersion = ds.getVersionNo();
        if ( null == currentVersion ){
            ds.setVersionNo(VERSION+lastPatch);
        }
        else if (currentVersion.contains("Patch")){
            ds.setVersionNo(currentVersion.substring(0, currentVersion.indexOf("Patch")+5)+lastPatch);
        }
        else{
            ds.setVersionNo(currentVersion+" Patch"+lastPatch);
        }
    }
}
