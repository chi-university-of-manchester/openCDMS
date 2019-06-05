package org.psygrid.data.clintouch;

public class QuestionMetadata {
	public enum BranchType { LESS_THAN, GREATER_THAN, LESS_THAN_SUM, GREATER_THAN_SUM, ONE_IS_LESS, ONE_IS_GREATER, ALL_ARE_LESS, ALL_ARE_GREATER, EQUAL_TO };
	
	private boolean isReversed = false;
	
	private boolean isBranched = false;
	private BranchType branchType;
	private int branchValueToCheck;
	private int questionsToSkip;
	private int numberOfOtherQuestions;  
	
	public QuestionMetadata(boolean isReversed, boolean isBranched) {
		this.isReversed = isReversed;
		this.isBranched = isBranched;
	}
	
	public QuestionMetadata(
			boolean isReversed,
			boolean isBranched,
			BranchType branchType,
			int branchValueToCheck,
			int questionToSkip,
			int numberOfOtherQuestions) {
		this.isReversed = isReversed;
		this.isBranched = isBranched;
		this.branchType = branchType;
		this.branchValueToCheck = branchValueToCheck;
		this.questionsToSkip = questionToSkip;
		this.numberOfOtherQuestions = numberOfOtherQuestions;
	}

	public boolean isReversed() {
		return isReversed;
	}
	
	public boolean isBranched() {
		return isBranched;
	}

	public BranchType getBranchType() {
		return branchType;
	}

	public int getBranchValueToCheck() {
		return branchValueToCheck;
	}

	public int getQuestionsToSkip() {
		return questionsToSkip;
	}

	public int getNumberOfOtherQuestions() {
		return numberOfOtherQuestions;
	}
}
