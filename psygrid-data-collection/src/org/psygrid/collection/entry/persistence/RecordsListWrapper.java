package org.psygrid.collection.entry.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.psygrid.collection.entry.persistence.RecordsList.Result;
import org.psygrid.data.model.hibernate.Identifier;

public class RecordsListWrapper {
	
    public final static class Item   {
        private Identifier identifier;
        private boolean readyToCommit;
        private String identifierRepresentation;
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
        public Item(Identifier identifier, boolean readyToCommit, String identifierRepresentation) {
            if (identifier == null) {
                throw new IllegalArgumentException("identifier cannot be null"); //$NON-NLS-1$
            }
            this.identifier = identifier;
            this.readyToCommit = readyToCommit;
            this.identifierRepresentation = identifierRepresentation;
        }
        
        public final Identifier getIdentifier() {
            return identifier;
        }
        
        public final String getIdentifierRepresentation(){
        	return identifierRepresentation;
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
            if (o instanceof Item == false && o instanceof RecordsList.Item == false) {
                return false;
            }
            
            if(o instanceof Item){
	            Item entry = (Item) o;
	            return identifier.getIdentifier().equals(entry.identifier.getIdentifier()) &&
	                    readyToCommit == entry.readyToCommit;
            }else{
            	RecordsList.Item entry = (RecordsList.Item) o;
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
    
    private List<Item> items;
    
    public RecordsListWrapper() {
        items = new ArrayList<Item>();
    }

    public final Item addItem(Identifier identifier, boolean readyToCommit, String identifierRepresentation) {
        Item item = new Item(identifier, readyToCommit, identifierRepresentation);
        int location = items.indexOf(item);
        if (location == -1) {
            items.add(item);
            return item;
        }
        return null;
    }
    
    public final List<RecordsListWrapper.Item> getItems() {
        return Collections.unmodifiableList(items);
    }
    
}
