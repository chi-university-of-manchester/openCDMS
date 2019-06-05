package org.psygrid.data.clintouch;

import java.util.ArrayList;
import java.util.List;

public class QuestionMetadataCollection {
	private List<QuestionMetadata> questionMetadataList = new ArrayList<QuestionMetadata>();
	
	public QuestionMetadataCollection(List<QuestionMetadata> questionBranches) {
		this.questionMetadataList = questionBranches;
	}
	
	public int getQuestionsToSkip(int questionId, int answer, List<Integer> previousAnswers) {
		QuestionMetadata questionMetadata = questionMetadataList.get(questionId);
		if(!questionMetadata.isBranched()) {
			return 0;
		}
		switch(questionMetadata.getBranchType()) {
		case LESS_THAN:
			if(answer < questionMetadata.getBranchValueToCheck()) {
				return questionMetadata.getQuestionsToSkip();
			}
			return 0;
		case GREATER_THAN:
			if(answer > questionMetadata.getBranchValueToCheck()) {
				return questionMetadata.getQuestionsToSkip();
			}
			return 0;
		case LESS_THAN_SUM:
			if(calculateSum(questionMetadata, answer, previousAnswers) < questionMetadata.getBranchValueToCheck()) {
				return questionMetadata.getQuestionsToSkip();
			}
			return 0;
		case GREATER_THAN_SUM:
			if(calculateSum(questionMetadata, answer, previousAnswers) > questionMetadata.getBranchValueToCheck()) {
				return questionMetadata.getQuestionsToSkip();
			}
			return 0;
		case ONE_IS_LESS:
			if(answer < questionMetadata.getBranchValueToCheck() || otherQuestionsLess(questionMetadata, previousAnswers)) {
				return questionMetadata.getQuestionsToSkip();
			}
			return 0;
		case ONE_IS_GREATER:
			if(answer > questionMetadata.getBranchValueToCheck() || otherQuestionsGreater(questionMetadata, previousAnswers)) {
				return questionMetadata.getQuestionsToSkip();
			}
			return 0;
		case ALL_ARE_LESS:
			if(answer < questionMetadata.getBranchValueToCheck() && otherQuestionsLess(questionMetadata, previousAnswers)) {
				return questionMetadata.getQuestionsToSkip();
			}
			return 0;
		case ALL_ARE_GREATER:
			if(answer > questionMetadata.getBranchValueToCheck() && otherQuestionsGreater(questionMetadata, previousAnswers)) {
				return questionMetadata.getQuestionsToSkip();
			}
			return 0;
		case EQUAL_TO:
			if(answer == questionMetadata.getBranchValueToCheck()) {
				return questionMetadata.getQuestionsToSkip();
			}
			return 0;
		}
		
		return 0;
	}
	
	public boolean isReversed(int questionId) {
		return questionMetadataList.get(questionId).isReversed();
	}
	
	private int calculateSum(QuestionMetadata questionMetadata, int answer, List<Integer> previousAnswers) {
		int sum = answer;
		// Sum answers starting from last of previousAnswers
		for(int answerIndex = 0; answerIndex < questionMetadata.getNumberOfOtherQuestions(); answerIndex++) {
			sum += previousAnswers.get(previousAnswers.size() - answerIndex - 1);
		}
		return sum;
	}
	
	private boolean otherQuestionsLess(QuestionMetadata questionMetadata, List<Integer> previousAnswers) {
		for(int answerIndex = 0; answerIndex < questionMetadata.getNumberOfOtherQuestions(); answerIndex++) {
			if(previousAnswers.get(previousAnswers.size() - answerIndex - 1) < questionMetadata.getBranchValueToCheck()) {
				return true;
			}
		}
		return false;
	}
	
	private boolean otherQuestionsGreater(QuestionMetadata questionMetadata, List<Integer> previousAnswers) {
		for(int answerIndex = 0; answerIndex < questionMetadata.getNumberOfOtherQuestions(); answerIndex++) {
			if(previousAnswers.get(previousAnswers.size() - answerIndex - 1) > questionMetadata.getBranchValueToCheck()) {
				return true;
			}
		}
		return false;
	}
}
