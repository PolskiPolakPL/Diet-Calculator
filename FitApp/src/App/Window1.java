package App;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Window1 {
    JFrame frame;
    JPanel northPanel, southPanel, centerPanel;
    JLabel weightLabel, heightLabel, sexLabel, ageLabel, activityLabel;
    JLabel bmiLabel, ppmLabel, cpmLabel;
    JTextField weightTextField, heightTextField;
    Choice sexChoise, ageChoise, activityChoise;
    JButton calculateButton, printButton;

    JLabel carbSliderLabel, carbSliderValue, fatSliderLabel, fatSliderValue, proteinSliderLabel, proteinSliderValue;
    JSlider carbSlider, fatSlider, proteinSlider;
    JCheckBox snack1Checkbox, snack2Checkbox;

    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu("Language");

    int ppm, cpm;

    //Builder aplikacji
    Window1(){
        //okno
        frame = new JFrame("Starting window");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(500,300);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        //Panele
        northPanel = new JPanel();
        centerPanel = new JPanel();
        southPanel = new JPanel();

        //Center Panel
        GroupLayout groupLayout = new GroupLayout(centerPanel);
        centerPanel.setLayout(groupLayout);
        groupLayout.setAutoCreateGaps(true);
        groupLayout.setAutoCreateContainerGaps(true);

        //sex
        sexLabel = new JLabel("sex: ");//sex
        sexChoise = new Choice();
        sexChoise.add("Male");//Male
        sexChoise.add("Female");//Female
        //age
        ageLabel = new JLabel("age: ");//age
        ageChoise = new Choice();
        for (int i=1;i<=120;i++){
            ageChoise.add(String.valueOf(i));
        }
        //weight
        weightLabel = new JLabel("weight: ");//weight
        weightTextField = new JTextField(4);
        //height
        heightLabel = new JLabel("height: ");//height
        heightTextField = new JTextField(4);
        //activity
        activityLabel = new JLabel("Physical Activity Level ");//PA Level
        activityChoise = new Choice();
        activityChoise.add("sedentary lifestyle");//PAL1
        activityChoise.add("light active lifestyle");//PAL2
        activityChoise.add("moderately active lifestyle");//PAL3
        activityChoise.add("active lifestyle");//PAL4
        activityChoise.add("vigorously active lifestyle");//PAL4
        activityChoise.add("professional sports person");//PAL5
        //sliders
        carbSliderLabel = new JLabel("carbohydrates ");//carbohydrates
        carbSlider = new JSlider(45,60,50);
        carbSliderValue = new JLabel(carbSlider.getValue()+"%");

        proteinSliderLabel = new JLabel("proteins ");//proteins
        proteinSlider = new JSlider(15,20,20);
        proteinSliderValue = new JLabel(proteinSlider.getValue()+"%");

        fatSliderLabel = new JLabel("fats ");//fats
        fatSlider = new JSlider(25,35,30);
        fatSliderValue = new JLabel(fatSlider.getValue()+"%");

        //Checkboxes
        snack1Checkbox = new JCheckBox("Morning Snacks");//Morning Snacks
        snack2Checkbox = new JCheckBox("Evening Snacks");//Evening Snacks


        //bmi
        bmiLabel = new JLabel("BMI :");
        //ppm
        ppmLabel = new JLabel("PPM: ");
        //cpm
        cpmLabel = new JLabel("CPM: ");

        //przyciski
        calculateButton = new JButton("Calculate");//Calculate
        printButton = new JButton("Print Diet Plan");//Print Diet Plan

        //dodawanie
        fillNorthPanel();

        handleGroupLayout(groupLayout);

        southPanel.add(bmiLabel);
        southPanel.add(ppmLabel);
        southPanel.add(cpmLabel);


        loadLanguagesMenu();
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);
        frame.add(northPanel,BorderLayout.NORTH);
        frame.add(centerPanel,BorderLayout.CENTER);
        frame.add(southPanel,BorderLayout.SOUTH);

        //eventy
        calculateButton.addActionListener(e->{
            boolean isMale = sexChoise.getSelectedIndex()==0;//Male
            int age, weight, height;
            double bmi, pal, averagePPM, preciseCPM;
            try{
                //data extraction
                age = Integer.parseInt(ageChoise.getSelectedItem());
                weight = Integer.parseInt(weightTextField.getText());
                height = Integer.parseInt(heightTextField.getText());
                pal = getPalValue(activityChoise.getSelectedIndex());
                bmi = calculateBMI(weight,height);
                //calculations
                averagePPM = (mifflin(isMale,weight,height,age)+harrisBenedict(isMale,weight,height,age))/2;
                ppm = (int)(Math.round(averagePPM/100)*100);
                preciseCPM = averagePPM * pal;
                cpm = (int)(Math.round(preciseCPM/100)*100);
                //output
                bmiLabel.setText("BMI: "+bmi);
                bmiLabel.setForeground(setBMIcolor(bmi));
                ppmLabel.setText("PPM: "+ppm+"kcal");
                cpmLabel.setText("CPM: "+cpm+"kcal");
            }catch (IllegalArgumentException illegalArgumentException){
                System.out.println("Illegal argument on Input!\n" + illegalArgumentException.getMessage());
            }catch (Exception exception){
                System.out.println("Unknown exception found.\n" + exception.getMessage());
            }
        });

        printButton.addActionListener(e->{
            ArrayList<Integer> ppmMealsList = calculateMeals(ppm);
            ArrayList<Integer> cpmMealsList = calculateMeals(cpm);

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

    //prywatne metody ('funkcje')
    //bmi
    private double calculateBMI(int weight, int height){
        double bmi = weight/((height*height)/10000.0);
        bmi = Math.round(bmi*100);
        return bmi/100;
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
    //kcal
    private double mifflin(boolean isMale, int weight, int height, int age){
        if(isMale)
            return 10*weight+6.25*height-5*age+5;
        else
            return 10*weight+6.25*height-5*age-161;
    }
    private double harrisBenedict(boolean isMale, int weight, int height, int age){
        if(isMale)
            return 88.362+13.397*weight+4.799*height-5.677*age;
        else
            return 447.593+9.247*weight+3.098*height-4.33*age;
    }
    private double getPalValue(int index){
        return switch (index) {
            case 0 -> 1.25;
            case 1 -> 1.4;
            case 2 -> 1.6;
            case 3 -> 1.7;
            case 4 -> 2.0;
            case 5 -> 2.3;
            default -> 1;
        };
    }
    private int[] calculateNutrition(int totalKcal, String type){
        int carbs, fat, proteins;
        carbs = carbSlider.getValue();
        fat = fatSlider.getValue();
        proteins = proteinSlider.getValue();
        System.out.println("--- "+type+" ---");
        System.out.println("Carbohydrates: "+prcntInt(totalKcal,carbs)+"kcal ("+carbs+"%)");
        System.out.println("          Fat: "+prcntInt(totalKcal,fat)+"kcal ("+fat+"%)");
        System.out.println("     Proteins: "+prcntInt(totalKcal,proteins)+"kcal ("+proteins+"%)");
        return new int[]{prcntInt(totalKcal,carbs),prcntInt(totalKcal,proteins),prcntInt(totalKcal,fat)};
    }
    private ArrayList<Integer> calculateMeals(int totalKcal){
        ArrayList<Integer> meals = new ArrayList<>();
        int breakfast, lunch, dinner;
        int morSnack, eveSnack;
        if(snack1Checkbox.isSelected()){
            meals.add(prcntInt(totalKcal,25));//breakfast
            meals.add(prcntInt(totalKcal,10));//morningSnack
            meals.add(prcntInt(totalKcal,35));//lunch
        }
        else{
            meals.add(prcntInt(totalKcal,30));//breakfast
            meals.add(prcntInt(totalKcal,40));//lunch
        }
        if(snack2Checkbox.isSelected()){
            meals.add(prcntInt(totalKcal,10));//eveningSnack
            meals.add(prcntInt(totalKcal,20));//dinner
        }
        else{
            meals.add(prcntInt(totalKcal,30));//dinner
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
    private void fillNorthPanel(){
        JLabel kgLabel = new JLabel("kg");
        JLabel cmLabel = new JLabel("cm");
        northPanel.add(sexLabel);
        northPanel.add(sexChoise);
        northPanel.add(ageLabel);
        northPanel.add(ageChoise);
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
                                .addComponent(activityChoise)
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
                                .addComponent(activityChoise)
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
        groupLayout.linkSize(SwingConstants.VERTICAL,activityChoise,activityLabel, calculateButton);
    }

    private int prcntInt(int value, int percent){
        return value*percent/100;
    }
}
