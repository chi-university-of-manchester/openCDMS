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


package org.psygrid.collection.entry.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.psygrid.data.model.hibernate.Identifier;

public class RecordsList {
    
    public final static class Item   {
        private Identifier identifier;
        private boolean readyToCommit;
        @Deprecated
        private boolean actionResult;
        private Result result = Result.FAILURE;
        
        public Item() {
            // Empty constructor for xstream
        }
        /**
         * 
         * @param identifier
         * @param readyToCommit determines whether this record is ready to
         * be committed to the repository.
         */
        public Item(Identifier identifier, boolean readyToCommit) {
            if (identifier == null) {
                throw new IllegalArgumentException("identifier cannot be null"); //$NON-NLS-1$
            }
            this.identifier = identifier;
            this.readyToCommit = readyToCommit;
        }
        
        public final Identifier getIdentifier() {
            return identifier;
        }
        
        public final boolean isReadyToCommit() {
            return readyToCommit;
        }
        
        @Override
        public int hashCode() {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + identifier.getIdentifier().hashCode();
            result = PRIME * result + (readyToCommit ? 1231 : 1237);
            return result;
        }
        
        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof Item == false && o instanceof RecordsListWrapper.Item == false) {
                return false;
            }
            
            if(o instanceof Item){
            Item entry = (Item) o;
            return identifier.getIdentifier().equals(entry.identifier.getIdentifier()) &&
                    readyToCommit == entry.readyToCommit;
            }else{
            	RecordsListWrapper.Item entry = (RecordsListWrapper.Item) o;
            	return identifier.getIdentifier().equals(entry.getIdentifier().getIdentifier()) &&
                readyToCommit == entry.isReadyToCommit();
            }
        }
        
        /**
         * Returns the result of an action taken against an item.
         * @return
         */
		public Result getResult() {
			return result;
		}
		
		/**
		 * Set the result of an action taken against an item. 
		 * For example, the 'action' might be committing the item to the repository,
		 * and the 'result' might be 'false', indicating failure.
		 * @param actionResult
		 */
		public void setResult(Result result) {
			this.result = result;
		}
        
		/**
		 * Required by xstream to preserve backwards compatibility with
		 * RecordsList objects serialized before the result property
		 * replaced the actionResult property.
		 * 
		 * @return this
		 */
		private Object readResolve(){
			if ( null == result ){
				if ( actionResult){
					result = Result.SUCCESS;
				}
				else{
					result = Result.FAILURE;
				}
			}
			return this;
		}
    }
    
    public void sort() {
    	CompareItem compare = new CompareItem();
    	java.util.Collections.sort(items, compare);
    }
    
    public class CompareItem implements Comparator<Item> {
        
    	/**
         * Compare the identifier for this item to the given item.
         * 
         * Returns 0 if identical, 1 if the given item is larger and -1 if the 
         * given item is smaller.
         * 
         * @param o
         * @return int
         */
        public int compare(Item one, Item two) {
        	if (one == two ) {
        		return 0;
        	}
        	return one.getIdentifier().compareTo(two.getIdentifier());
        }
        
    }
    
    private List<Item> items;
    
    public RecordsList() {
        items = new ArrayList<Item>();
    }
    
    public final Item addItem(Identifier identifier, boolean readyToCommit) {
        Item item = new Item(identifier, readyToCommit);
        int location = items.indexOf(item);
        if (location == -1) {
            items.add(item);
            return item;
        }
        return null;
    }
    
    public final boolean hasItem(RecordsList.Item item) {
        return items.indexOf(item) != -1;
    }
    
    public final boolean hasItem(RecordsListWrapper.Item item) {
        return items.indexOf(item) != -1;
    }
    
    public final boolean removeItem(Identifier identifier,
            boolean readyToCommit) {
        return items.remove(new Item(identifier, readyToCommit));
    }
    
    public final boolean removeItem(RecordsListWrapper.Item item) {
        return items.remove(item);
    }
    
    public final boolean removeItem(Item item) {
        return items.remove(item);
    }
    
    public final RecordsList.Item getItem(Identifier identifier,
            boolean readyToCommit){
        int index = items.indexOf(new Item(identifier, readyToCommit));
        if (index == -1) {
            return null;
        }
        return items.get(index);
    }
    
    public final List<RecordsList.Item> getItems() {
        return Collections.unmodifiableList(items);
    }
    
    public final int getSize() {
        return items.size();
    }

    public final void removeItem(int index) {
        items.remove(index);        
    }
    
    public enum Result {
    	SUCCESS,
    	FAILURE,
    	DUPLICATES;

		@Override
		public String toString() {
			switch (this) {
			case SUCCESS:
				return "Success";
			case FAILURE:
				return "Failed";
			case DUPLICATES:
				return "Duplicate documents discarded";
			}
			//Should never happen
			return null;
		}
    	
    }
    
}
