package it.unipi.dii.aide.lsmsd.recipeshare.service;

import it.unipi.dii.aide.lsmsd.recipeshare.dao.mongo.ReportedRecipeMongoDAO;
import it.unipi.dii.aide.lsmsd.recipeshare.model.Author;
import it.unipi.dii.aide.lsmsd.recipeshare.model.Recipe;
import it.unipi.dii.aide.lsmsd.recipeshare.model.ReportedRecipe;

import java.util.List;

public class ReportedRecipeService {
    public static boolean addReportedRecipe(ReportedRecipe reportedRecipe) {
        return ReportedRecipeMongoDAO.addReportedRecipe(reportedRecipe);
    }

    public static boolean approveReportedRecipe(Recipe recipe) {
        try{
            ReportedRecipeMongoDAO.removeReportedRecipe(recipe.getName());
            return true;
        }catch (Exception e){
            //TODO rollback
            return false;
        }
    }
    public static boolean notApproveReportedRecipe(Recipe recipe) {
        try{
            ReportedRecipeMongoDAO.removeReportedRecipe(recipe.getName());
            if(!RecipeService.deleteRecipe(recipe)){
                //TODO rollback
                //di:
                // ReportedRecipeMongoDAO.removeReportedRecipe(recipe.getName());
            }
            return true;
        }catch (Exception e){
            //TODO rollback
            return false;
        }
    }

    public static List<ReportedRecipe> getListReportedRecipes(String nameToSearch, Integer elementToSkip, Integer elementsToLimit) {
        return ReportedRecipeMongoDAO.getListReportedRecipes(nameToSearch, elementToSkip, elementsToLimit);
    }

    public static List<Author> onHighestRatioQueryClick(){
        return ReportedRecipeMongoDAO.onHighestRatioQueryClick();
    }

}