package App;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Window1 {
    JFrame frame = new JFrame("Fit App");
    JPanel northPanel = new JPanel();
    JPanel centerPanel = new JPanel();
    JPanel southPanel = new JPanel();

    //Text Fields
    JTextField weightTextField = new JTextField(4);
    JTextField heightTextField = new JTextField(4);

    //Choices
    Choice sexChoice = new Choice();
    Choice ageChoice = new Choice();
    Choice activityChoice = new Choice();

    //Labels
    JLabel sexLabel = new JLabel("sex: ");//sex
    JLabel bmiLabel = new JLabel("BMI :");
    JLabel peiLabel = new JLabel("PPM: ");
    JLabel teiLabel = new JLabel("CPM: ");
    JLabel activityLabel = new JLabel("Physical Activity Level ");//PA Level
    JLabel ageLabel = new JLabel("age: ");//age
    JLabel weightLabel = new JLabel("weight: ");//weight
    JLabel heightLabel = new JLabel("height: ");//height
    JLabel carbSliderLabel = new JLabel("carbohydrates ");//carbohydrates
    JLabel proteinSliderLabel = new JLabel("proteins ");//proteins
    JLabel fatSliderLabel = new JLabel("fats ");//fats

    //CheckBoxes
    JCheckBox snack1Checkbox = new JCheckBox("Morning Snacks");//Morning Snacks
    JCheckBox snack2Checkbox = new JCheckBox("Evening Snacks");//Evening Snacks

    //Buttons
    JButton calculateButton = new JButton("Calculate");//Calculate
    JButton printButton = new JButton("Print Diet Plan");//Print Diet Plan

    JLabel carbSliderValue, fatSliderValue, proteinSliderValue;
    JSlider carbSlider, fatSlider, proteinSlider;

    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu("Language");

    Human user;

    //Builder aplikacji
    Window1(){
        //okno
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(500,300);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        //Center Panel
        GroupLayout groupLayout = new GroupLayout(centerPanel);
        centerPanel.setLayout(groupLayout);
        groupLayout.setAutoCreateGaps(true);
        groupLayout.setAutoCreateContainerGaps(true);

        //sliders
        carbSlider = new JSlider(45,60,50);
        carbSliderValue = new JLabel(carbSlider.getValue()+"%");

        proteinSlider = new JSlider(15,20,20);
        proteinSliderValue = new JLabel(proteinSlider.getValue()+"%");

        fatSlider = new JSlider(25,35,30);
        fatSliderValue = new JLabel(fatSlider.getValue()+"%");


        //dodawanie
        AddNorthPanel();
        handleGroupLayout(groupLayout);
        southPanel.add(bmiLabel);
        southPanel.add(peiLabel);
        southPanel.add(teiLabel);
        loadLanguagesMenu();
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);
        frame.add(northPanel,BorderLayout.NORTH);
        frame.add(centerPanel,BorderLayout.CENTER);
        frame.add(southPanel,BorderLayout.SOUTH);

        //eventy
        calculateButton.addActionListener(e->{

            try{
                user = GetUserData();
                PrintOutput();
            }catch (IllegalArgumentException illegalArgumentException){
                System.out.println("Illegal argument on Input!\n" + illegalArgumentException.getMessage());
            }catch (Exception exception){
                System.out.println("Unknown exception found.\n" + exception.getMessage());
            }
        });

        printButton.addActionListener(e->{
            ArrayList<Integer> ppmMealsList = GetCalculatedMeals((int)user.GetPassiveEnergyIntake());
            ArrayList<Integer> cpmMealsList = GetCalculatedMeals((int)user.GetTotalEnergyIntake());

            int[][] ppmMealsXNutrition = new int[ppmMealsList.size()][3];
            int[][] cpmMealsXNutrition = new int[cpmMealsList.size()][3];

            for(int i=0;i<ppmMealsList.size();i++){
                ppmMealsXNutrition[i] = calculateNutrition(ppmMealsList.get(i),"ppm "+i);
            }
            for(int i=0;i<cpmMealsList.size();i++){
                cpmMealsXNutrition[i] = calculateNutrition(cpmMealsList.get(i),"cpm "+i);
            }

            Pliki.zapiszPlik("CsvFiles\\","EmptyDietPlan.csv",generateCSV(ppmMealsXNutrition,cpmMealsXNutrition));
        });

        carbSlider.addChangeListener(e->{
            carbSliderValue.setText(carbSlider.getValue()+"%");
            if(check100percent()!=0){
                correctSliders(loadSliderList(proteinSlider,fatSlider));
            }
        });
        proteinSlider.addChangeListener(e->{
            proteinSliderValue.setText(proteinSlider.getValue()+"%");
            if(check100percent()!=0){
                correctSliders(loadSliderList(fatSlider,carbSlider));
            }
        });
        fatSlider.addChangeListener(e->{
            fatSliderValue.setText(fatSlider.getValue()+"%");
            if(check100percent()!=0){
                correctSliders(loadSliderList(carbSlider,proteinSlider));
            }
        });
    }

    //publiczne metody
    public void show(){
        frame.setVisible(true);
    }

    //prywatne metody

    private Human GetUserData(){
        Sex sex = GetSex(sexChoice.getSelectedIndex());
        int age = Integer.parseInt(ageChoice.getSelectedItem());
        int height = Integer.parseInt(heightTextField.getText());
        int weight = Integer.parseInt(weightTextField.getText());
        int palIndex = activityChoice.getSelectedIndex();
        return new Human(sex,age,height,weight,palIndex);
    }

    private Sex GetSex(int index){
        if(index==0)
            return Sex.MALE;
        else
            return Sex.FEMALE;
    }

    private void PrintOutput(){
        bmiLabel.setText("BMI: "+user.GetBMI());
        bmiLabel.setForeground(setBMIcolor(user.GetBMI()));
        peiLabel.setText("PPM: "+ user.GetPassiveEnergyIntake() +"kcal");
        teiLabel.setText("CPM: "+ user.GetTotalEnergyIntake() +"kcal");
    }

    private Color setBMIcolor(double bmi){
        if(bmi<18.5)
            return new Color(69, 69, 240);
        if(bmi<25)
            return new Color(21, 159, 30);
        if(bmi<30)
            return new Color(206, 191, 19);
        if(bmi<35)
            return new Color(239, 130, 5);
        if(bmi<40)
            return new Color(238, 36, 36);
        else
            return new Color(255, 0, 0);
    }
    private int[] calculateNutrition(int totalKcal, String type){
        int carbs, fat, proteins;
        carbs = carbSlider.getValue();
        fat = fatSlider.getValue();
        proteins = proteinSlider.getValue();
        System.out.println("--- "+type+" ---");
        System.out.println("Carbohydrates: "+ GetPercentOfValue(totalKcal,carbs)+"kcal ("+carbs+"%)");
        System.out.println("          Fat: "+ GetPercentOfValue(totalKcal,fat)+"kcal ("+fat+"%)");
        System.out.println("     Proteins: "+ GetPercentOfValue(totalKcal,proteins)+"kcal ("+proteins+"%)");
        return new int[]{GetPercentOfValue(totalKcal,carbs), GetPercentOfValue(totalKcal,proteins), GetPercentOfValue(totalKcal,fat)};
    }
    private ArrayList<Integer> GetCalculatedMeals(int totalKcal){
        ArrayList<Integer> meals = new ArrayList<>();
        if(snack1Checkbox.isSelected()){
            meals.add(GetPercentOfValue(totalKcal,25));//breakfast
            meals.add(GetPercentOfValue(totalKcal,10));//morningSnack
            meals.add(GetPercentOfValue(totalKcal,35));//lunch
        }
        else{
            meals.add(GetPercentOfValue(totalKcal,30));//breakfast
            meals.add(GetPercentOfValue(totalKcal,40));//lunch
        }
        if(snack2Checkbox.isSelected()){
            meals.add(GetPercentOfValue(totalKcal,10));//eveningSnack
            meals.add(GetPercentOfValue(totalKcal,20));//dinner
        }
        else{
            meals.add(GetPercentOfValue(totalKcal,30));//dinner
        }
        return meals;
    }
    //files
    private void loadLanguagesMenu(){
        JMenuItem item;
        String[] filenames = Pliki.getFilesNames("Language",".txt");
        if (filenames != null)
            for(String name:filenames){
                item = new JMenuItem(name.substring(0,name.length()-4));
                menu.add(item);
                item.addActionListener(e->{
                    switchLanguage(name);
                });
            }
    }
    private String[] switchLanguage(String fileName){
        ArrayList<String> languageList =  Pliki.wczytajPlik("Language" +"\\",fileName);
        sexLabel.setText(languageList.get(0));
        ageLabel.setText(languageList.get(3));
        weightLabel.setText(languageList.get(4));
        heightLabel.setText(languageList.get(5));
        activityLabel.setText(languageList.get(6));
        carbSliderLabel.setText(languageList.get(13));
        proteinSliderLabel.setText(languageList.get(14));
        fatSliderLabel.setText(languageList.get(15));
        snack1Checkbox.setText(languageList.get(17));
        snack2Checkbox.setText(languageList.get(19));
        calculateButton.setText(languageList.get(21));
        printButton.setText(languageList.get(22));
        return languageList.toArray(new String[0]);
    }
    private String[] generateCSV(int[][]ppmValues, int[][] cpmValues){
        ArrayList<String> csvLinesList = new ArrayList();
        ArrayList<Integer> ppmMealsSum = sumMealsValue(ppmValues);
        ArrayList<Integer> cpmMealsSum = sumMealsValue(cpmValues);
        ArrayList<String> mealNameList = new ArrayList<>();
        mealNameList.add("Breakfast");
        if(snack1Checkbox.isSelected())
            mealNameList.add("Morning Snack");
        mealNameList.add("Lunch");
        if(snack2Checkbox.isSelected())
            mealNameList.add("Evening Snack");
        mealNameList.add("Dinner");


        csvLinesList.add("-;Monday;Tuesday;Wednesday;Thursday;Friday;Saturday;Sunday;carbohydrates("+carbSlider.getValue()+"%);proteins("+proteinSlider.getValue()+"%);fat("+fatSlider.getValue()+"%)");
        for (int i=0;i<mealNameList.size();i++) {
            csvLinesList.add(mealNameList.get(i)+" ("+ppmMealsSum.get(i)+"-"+cpmMealsSum.get(i)+"kcal);;;;;;;;"+ppmValues[i][0]+"-"+cpmValues[i][0]+"kcal;"+ppmValues[i][1]+"-"+cpmValues[i][1]+"kcal;"+ppmValues[i][2]+"-"+cpmValues[i][2]+"kcal");
        }
        csvLinesList.add("sum:");

        return csvLinesList.toArray(new String[0]);
    }
    private ArrayList<Integer> sumMealsValue(int[][] mealsXnutrition){
        ArrayList<Integer> mealsSum = new ArrayList<>();
        for (int[] meals : mealsXnutrition) {
            int sum = 0;
            for (int nutrition : meals) {
                sum += nutrition;
            }
            mealsSum.add(sum);
        }
        return mealsSum;
    }
    //sliders
    private ArrayList<JSlider> loadSliderList(JSlider slider1, JSlider slider2){
        ArrayList sliderList = new ArrayList();
        sliderList.add(slider1);
        sliderList.add(slider2);
        return sliderList;
    }
    private int check100percent(){
        int carbs = carbSlider.getValue();
        int fat = fatSlider.getValue();
        int protein = proteinSlider.getValue();
        return Integer.compare(carbs + fat + protein, 100);
    }
    private void correctSliders(List<JSlider> sliderList){
        while(check100percent()>0){
            for (JSlider nextSlider:sliderList) {
                if(nextSlider.getMinimum()<nextSlider.getValue()){
                    nextSlider.setValue(nextSlider.getValue()-1);
                    break;
                }
            }
        }
        while (check100percent()<0){
            for (JSlider nextSlider:sliderList) {
                if(nextSlider.getMaximum()>nextSlider.getValue()){
                    nextSlider.setValue(nextSlider.getValue()+1);
                    break;
                }
            }
        }
    }
    //layout handler
    private void AddNorthPanel(){
        //Fill Sex Choice
        sexChoice.add("Male");//Male
        sexChoice.add("Female");//Female

        // Fill Age Choice
        for (int i=1;i<=120;i++){
            ageChoice.add(String.valueOf(i));
        }

        //Fill Activity Choice
        activityChoice.add("sedentary lifestyle");//PAL1
        activityChoice.add("light active lifestyle");//PAL2
        activityChoice.add("moderately active lifestyle");//PAL3
        activityChoice.add("active lifestyle");//PAL4
        activityChoice.add("vigorously active lifestyle");//PAL4
        activityChoice.add("professional sports person");//PAL5

        JLabel kgLabel = new JLabel("kg");
        JLabel cmLabel = new JLabel("cm");
        northPanel.add(sexLabel);
        northPanel.add(sexChoice);
        northPanel.add(ageLabel);
        northPanel.add(ageChoice);
        northPanel.add(weightLabel);
        northPanel.add(weightTextField);
        northPanel.add(kgLabel);
        northPanel.add(heightLabel);
        northPanel.add(heightTextField);
        northPanel.add(cmLabel);
    }
    private void handleGroupLayout(GroupLayout groupLayout){
        groupLayout.setHorizontalGroup(
                groupLayout.createSequentialGroup()
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(activityLabel)
                                .addComponent(carbSliderLabel)
                                .addComponent(proteinSliderLabel)
                                .addComponent(fatSliderLabel))
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(activityChoice)
                                .addComponent(carbSlider)
                                .addComponent(proteinSlider)
                                .addComponent(fatSlider)
                                .addGroup(groupLayout.createSequentialGroup()
                                    .addComponent(snack1Checkbox)
                                    .addComponent(snack2Checkbox))
                                .addComponent(printButton))//nwm napraw to xd
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(calculateButton)
                                .addComponent(carbSliderValue)
                                .addComponent(proteinSliderValue)
                                .addComponent(fatSliderValue))
        );
        groupLayout.setVerticalGroup(
                groupLayout.createSequentialGroup()
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(activityLabel)
                                .addComponent(activityChoice)
                                .addComponent(calculateButton))
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(carbSliderLabel)
                                .addComponent(carbSlider)
                                .addComponent(carbSliderValue))
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(proteinSliderLabel)
                                .addComponent(proteinSlider)
                                .addComponent(proteinSliderValue))
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(fatSliderLabel)
                                .addComponent(fatSlider)
                                .addComponent(fatSliderValue))
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(snack1Checkbox)
                                .addComponent(snack2Checkbox))
                        .addComponent(printButton)
        );
        groupLayout.linkSize(SwingConstants.VERTICAL, activityChoice,activityLabel, calculateButton);
    }

    private int GetPercentOfValue(int value, int percent){
        return value*percent/100;
    }
}
