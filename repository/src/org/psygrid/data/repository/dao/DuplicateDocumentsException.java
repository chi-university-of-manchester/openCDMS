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

package org.psygrid.data.repository.dao;

import java.util.List;

public class DuplicateDocumentsException extends DAOException {

    private static final long serialVersionUID = -8059899412643971351L;

    private String title;
    
    private Long[] discards = new Long[0];
    
    private String duplicateList;
    
    public DuplicateDocumentsException() {
        super();
    }

    public DuplicateDocumentsException(String message) {
        super(message);
    }

    public DuplicateDocumentsException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateDocumentsException(Throwable cause) {
        super(cause);
    }

    public DuplicateDocumentsException(String message, String title, List<Long> discards, String duplicateList) {
        super(message);
        this.title = title;
        this.discards = new Long[discards.size()];
        for ( int i=0; i<discards.size(); i++ ){
            this.discards[i] = discards.get(i);
        }
        this.duplicateList = duplicateList;
    }

    public Long[] getDiscards() {
        return discards;
    }

    public void setDiscards(Long[] discards) {
        this.discards = discards;
    }

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDuplicateList() {
		return duplicateList;
	}

	public void setDuplicateList(String duplicateList) {
		this.duplicateList = duplicateList;
	}

}
