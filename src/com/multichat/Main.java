package com.multichat;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class Main {

   public static void main(String[] args) {
      ArrayList<Integer> a = new ArrayList<>(Arrays.asList(100, 4, 200, 1, 3, 2));
      Set<Integer> s = new HashSet<>();
//      HashMap<Integer, Integer> map = new HashMap<>();
      for (int i = 0; i < a.size(); i++) {
         s.add(a.get(i));

      }
      int ans = 1;
      for (int i = 0; i < a.size(); i++) {
         int temp = 1;
         int c = a.get(i);
         int c2 = c;
//         System.out.print(c + " ");


         if (s.contains(c)) {
            s.remove(c);
//            System.out.print(s);
//            System.out.print(c+1);
//            System.out.print(" "+ s.contains(c + 1));

            while (s.contains(c + 1)) {

//               System.out.print((c + 1) + " ");
               temp++;
               c = c + 1;
               s.remove(c);
            }
//            System.out.print(" "+(c2-1));
//            System.out.print(" "+ s.contains(c2 -1));

            while (s.contains(c2 - 1)) {
               temp++;
               c2 = c2 - 1;
               s.remove(c2 );
            }
         }
         ans = Math.max(temp, ans);
//         System.out.println();
      }
      System.out.println(ans);
   }
}

