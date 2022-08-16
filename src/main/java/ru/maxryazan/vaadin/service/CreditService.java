package ru.maxryazan.vaadin.service;

import org.springframework.stereotype.Service;
import ru.maxryazan.vaadin.model.Credit;
import ru.maxryazan.vaadin.repository.CreditRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@Service
public class CreditService {
    private final CreditRepository creditRepository;

    public CreditService(CreditRepository creditRepository) {
        this.creditRepository = creditRepository;
    }

    public void save(Credit credit){
        creditRepository.save(credit);
    }

    public String generateRandomUniqueNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
      do {
          for (int i = 0; i < 8; i++) {
              sb.append(random.nextInt(10));
          }
      }
      while (!isUnique(sb.toString()));
        return sb.toString();
    }
    public boolean isUnique(String num){
        return findByNumberOfCreditContract(num);
    }

    private boolean findByNumberOfCreditContract(String num) {
        return creditRepository.findByNumberOfCreditContract(num) == null;
    }

    public String generateDateDMY(){
        Date date = new Date();
        String pattern = "dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }

    public double generateSumWithPercent(String stringSum, double percent, String stringDuration) {
        int sum = Integer.parseInt(stringSum);
        int duration = Integer.parseInt(stringDuration);
        double percentByThisContributionDependsOfDuration = percent / 12 * duration * 0.01;
        return roundToDoubleWithTwoSymbolsAfterDot(sum + sum * percentByThisContributionDependsOfDuration);
    }
    public double roundToDoubleWithTwoSymbolsAfterDot(double a) {
        return (double) Math.round(a * 100) / 100;
    }

    public double generateEveryMonthPay(double sumWithPercents, String stringNumberOfPays) {
     double numberOfPays = Double.parseDouble(stringNumberOfPays);
        return roundToDoubleWithTwoSymbolsAfterDot(sumWithPercents / numberOfPays);
    }
}
