package cookbook.backend;

public class BudgetService {

    // A simple helper container to return multiple pieces of data to the frontend
    public static class BudgetResult {
        public boolean isSuccess;
        public String errorMessage;
        public String formattedBudget;

        public BudgetResult(boolean isSuccess, String errorMessage, String formattedBudget) {
            this.isSuccess = isSuccess;
            this.errorMessage = errorMessage;
            this.formattedBudget = formattedBudget;
        }
    }

    public static BudgetResult validateBudget(String budgetStr, double currentTotalCost) {
        if (budgetStr.isEmpty() || budgetStr.equals("Enter Budget")) {
            return new BudgetResult(false, "Please enter a budget!", null);
        }

        double numericNewBudget = 0.0;
        try {
            // Strip out "php", commas, and spaces to get the raw number
            numericNewBudget = Double.parseDouble(budgetStr.toLowerCase().replace("php", "").replace(",", "").trim());
        } catch (Exception ex) {
            return new BudgetResult(false, "Please enter a valid number!", null);
        }

        // Check against the current shopping cart cost
        if (currentTotalCost > 0.0 && numericNewBudget < currentTotalCost) {
            String errorMsg = "Too low! Current meal costs Php " + String.format("%.2f", currentTotalCost);
            return new BudgetResult(false, errorMsg, null);
        }

        // Format the valid budget correctly
        String finalBudget;
        if (!budgetStr.toLowerCase().startsWith("php")) {
            finalBudget = "Php " + String.format("%.2f", numericNewBudget);
        } else {
            finalBudget = budgetStr;
        }

        return new BudgetResult(true, null, finalBudget);
    }
}
