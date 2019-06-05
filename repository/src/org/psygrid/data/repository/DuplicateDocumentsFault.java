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

package org.psygrid.data.repository;

public class DuplicateDocumentsFault extends Exception {

    private static final long serialVersionUID = 8294194564097566681L;

    private String message;
    
    private String title;
    
    private Long[] discards = new Long[0];
    
    private String duplicateList;
    
    public DuplicateDocumentsFault() {
        super();
    }

    public DuplicateDocumentsFault(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public DuplicateDocumentsFault(String message) {
        super(message);
        this.message = message;
    }

    public DuplicateDocumentsFault(Throwable cause) {
        super(cause);
        this.message = cause.getMessage();
    }

    public DuplicateDocumentsFault(Throwable cause, String title, Long[] discards, String duplicateList) {
        super(cause);
        this.message = cause.getMessage();
        this.title = title;
        this.discards = discards;
        this.duplicateList = duplicateList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
