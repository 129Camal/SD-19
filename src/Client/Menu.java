package Client;

public class Menu {

    private int menuOption;

    public void setVisible() {
        switch (menuOption) {
            case 1:
                System.out.println("************* MENU ****************\n" +
                        "* 1 - Log In                      *\n" +
                        "* 2 - Sign In                     *\n" +
                        "* 0 - Exit                        *\n" +
                        "***********************************\n");
                break;

            case 2: System.out.println("************* MENU *******************\n"+
                    "* 1 - Acquire Server Micro           *\n"+
                    "* 2 - Acquire Server Large           *\n"+
                    "* 3 - Show Acquired Servers          *\n"+
                    "* 4 - Add Founds to Wallet           *\n"+
                    "* 5 - Auctions                       *\n"+
                    "* 6 - Personal Data                  *\n"+
                    "* 0 - Logout                         *\n"+
                    "**************************************\n");
                break;

            case 3: System.out.println("* 1 - End Service                    *\n"+
                    "* 0 - Back                           *\n"+
                    "**************************************\n");
                break;
        }
    }

    public int getMenuOption() {
        return menuOption;
    }

    public void setMenuOption(int menuOption) {
        this.menuOption = menuOption;
    }
}