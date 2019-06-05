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

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.repository.client.RepositoryClient;

/**
 * Interface to represent a patch to apply to a dataset.
 *
 * @author Rob Harper
 *
 */
public abstract class AbstractPatch {

    /**
     * Get the name of the patch.
     *
     * @return The name of the patch.
     */
    public abstract String getName();

    /**
     * Apply the patch to the dataset
     *
     * @param ds The dataset to apply the patch to.
     * @param saml SAML assertion for security system.
     */
    public abstract void applyPatch(DataSet ds, String saml) throws Exception;

    /**
     * Return whether the patch should be isolated or not. By isolated, this means
     * that all changes made by this patch should be saved independently of any
     * other patch.
     * <p>
     * Typically a patch should be isolated if preApplyPatch and postApplyPatch are
     * used to make record changes, in which case this method should be overridden
     * to return true.
     *
     * @return Boolean, True if patch should be isolated, False otherwise.
     */
    public boolean isolated(){
        return false;
    }

    /**
     * Actions to be performed prior to a patch being applied to a dataset.
     * <p>
     * Typically this is required if the dataset patch impacts existing records,
     * and these records need to be updated before the patch can be applied.
     *
     * @param ds The dataset being patched.
     * @param client Repository web-service client.
     * @param saml SAML assertion for security system.
     * @return Object that can be used to share information between this method and
     * postApplyPatch
     * @throws Exception
     */
    public Object preApplyPatch(DataSet ds, RepositoryClient client, String saml) throws Exception {
        return null;
    }

    /**
     * Actions to be performed after a patch has been applied to a dataset.
     * <p>
     * Typically this is required if the dataset patch impacts existing records,
     * and these records need to be updated after the patch has been applied.
     *
     * @param ds The dataset being patched.
     * @param obj Object that can be used to share information between this method and
     * preApplyPatch
     * @param client Repository web-service client.
     * @param saml SAML assertion for security system.
     * @throws Exception
     */
    public void postApplyPatch(DataSet ds, Object obj, RepositoryClient client, String saml) throws Exception {
        return;
    }

}
