%% C++

default = [50 60 70 80 90 100 110 120 130 140 150; 86.2029 87.206 104.856 125.032 138.781 160.514 181.782 223.863 183.673 275.092 208.045];

scenario1Pmax10 = [144 172 201 230 259 288 317 345 374 403 432; 159.913 192.361 226.251 289.211 304.812 310.525 288.359 312.938 349.725 372.561 403.733];
scenario1Pmax15 = [600 720 840 960 1080 1200 1320 1440 1560 1680 1800; 775.304 923.401 946.391 1072.73 1204.28 1346.98 1501.03 1643.92 1761.16 1889.76 2140.82];
scenario1Pmax20 = [9372 11246 13120 14994 16868 18744 20618 22492 24366 26240 28114; 14733.1 18471.3 23429.9 30744.5 40981.5 52047.5 67483.6 83574.6 102332 124427 151569];

scenario2Pmax10 = [1500 1800 2100 2400 2700 3000 3300 3600 3900 4200 4500; 1769.56 1827.89 2280.24 2671.24 3082.2 3123.82 3597.26 4051.74 4726.19 6138.44 7229.97];
scenario2Pmax15 = [1500 1800 2100 2400 2700 3000 3300 3600 3900 4200 4500; 2542.17 2905.06 3638.58 4310.43 5119.86 6027.79 6299.15 7771.92 8892.04 9274.87 10625.7];
scenario2Pmax20 = [1500 1800 2100 2400 2700 3000 3300 3600 3900 4200 4500; 4141.67 4626.69 5296.11 6657.42 7468.86 9063.43 10269.2 10864.9 12220.2 13728.6 15054.6];

scenario3Pmax5 = [2496 2995 3494 3993 4492 4992 5491 5990 6489 6988 7487; 2533.74 3577.37 3371.84 8525.89 5957.86 12524.6 19455.6 36420.3 48456.9 69223.4 62660.9];
scenario3Pmax10 = [2496 2995 3494 3993 4492 4992 5491 5990 6489 6988 7487; 4589.82 5948.36 7942.13 10694.5 13357.6 15040.9 21786.5 21912.8 19022.8 44770.6 43800.6];
scenario3Pmax15 = [2496 2995 3494 3993 4492 4992 5491 5990 6489 6988 7487; 7196.63 8722.67 11053.1 12355.6 14001.5 16300.7 18039.9 21954.6 23754.4 26397 28925.5];
scenario3Pmax20 = [2496 2995 3494 3993 4492 4992 5491 5990 6489 6988 7487; 9965.57 12382.2 14119.6 16844.5 19067.6 22104.3 23645.6 26508.7 29547.6 32109.2 36961.8];

scenario4Po0 = [4992 5990 6988 7986 8984 9984 10982 11980 12978 13976 14974; 5493.53 6647.12 9335.52 11420.2 15022.7 19080 24300 36493.3 42219.2 47607.3 51761.4];
scenario4Po1 = [4992 5990 6988 7986 8984 9984 10982 11980 12978 13976 14974; 12563.4 15862.1 19954.4 24610.8 27325 36898.4 48742.9 48554.4 55446.1 62106.9 75872.3];
scenario4Po10 = [4992 5990 6988 7986 8984 9984 10982 11980 12978 13976 14974; 19679.1 24339.3 30842.6 36478.7 45342 51476.3 59918.6 65456.3 76851.9 105603 91258.5];
scenario4Po30 = [4992 5990 6988 7986 8984 9984 10982 11980 12978 13976 14974; 27094.6 33245 39372.6 46578.1 53594.9 61886.5 70418.5 79581.7 94656.4 100833 112316];

scenario5Cw1Ps1 = [3996 4795 5594 6393 7192 7992 8791 9590 10389 11188 11987; 4029.18 4523.54 5873.24 7697.78 9603.74 11986.7 15096.2 19105.2 22629.4 27535.2 32392.3];
scenario5Cw1Ps10 = [3996 4795 5594 6393 7192 7992 8791 9590 10389 11188 11987; 8384.1 10581.5 13064.3 15644.2 18816.7 22278.7 26273.5 30393.3 35582.5 40997.3 48017.6];
scenario5Cw5Ps1 = [3996 4795 5594 6393 7192 7992 8791 9590 10389 11188 11987; 14207.2 17633.3 20896.2 23631.7 27853.1 32151.5 36760.6 42266.7 49564 58397.4 64775.5];
scenario5Cw5Ps10 = [3996 4795 5594 6393 7192 7992 8791 9590 10389 11188 11987; 19183.6 24376.6 30658.9 35148 41839.1 48548.8 55071.1 67186.6 78895.2 76903.7 88746.8];
scenario5Cw10Ps1 = [3996 4795 5594 6393 7192 7992 8791 9590 10389 11188 11987; 23020.9 27915.6 33141.8 39401.9 46805.3 55358.8 61785.7 72900.1 72760.2 85793.2 102017];
scenario5Cw10Ps10 = [3996 4795 5594 6393 7192 7992 8791 9590 10389 11188 11987; 31757 40202.5 58719.3 63635.7 77387.3 115927 120802 202556 157199 265148 256599];

%% Java

defaultJava = [50 60 70 80 90 100 110 120 130 140 150; 69 90 99 130 163 170 301 318 349 396 457];

scenario1Pmax10Java = [144 172 201 230 259 288 317 345 374 403 432; 163 214 229 280 312 313 328 395 426 435 467];
scenario1Pmax15Java = [600 720 840 960 1080 1200 1320 1440 1560 1680 1800; 771 853 1292 1432 1503 1602 1706 1728 1845 2164 2586];
scenario1Pmax20Java = [9372 11246 13120 14994 16868 18744 20618 22492 24366 26240 28114; 23582 30780 32925 34868 41028 52007 55691 52268 59562 63560 64737];

scenario2Pmax10Java = [1500 1800 2100 2400 2700 3000 3300 3600 3900 4200 4500; 1664 2710 2649 3339 3360 5391 7722 7504 8942 8153 12553];
scenario2Pmax15Java = [1500 1800 2100 2400 2700 3000 3300 3600 3900 4200 4500; 2526 3590 3491 4875 5300 6388 8151 7149 8280 9241 9743];
scenario2Pmax20Java = [1500 1800 2100 2400 2700 3000 3300 3600 3900 4200 4500; 4079 5565 6809 6904 7075 8270 9437 10463 10701 11751 12491];

scenario3Pmax5Java = [2496 2995 3494 3993 4492 4992 5491 5990 6489 6988 7487; 2675 3408 4485 8410 15294 32241 36340 40692 51785 76455 105855];
scenario3Pmax10Java = [2496 2995 3494 3993 4492 4992 5491 5990 6489 6988 7487; 5103 6239 9523 10748 13127 19379 27870 21157 30635 50144 54755];
scenario3Pmax15Java = [2496 2995 3494 3993 4492 4992 5491 5990 6489 6988 7487; 6530 7961 7210 7820 9036 9511 9979 12637 14320 16502 17650];
scenario3Pmax20Java = [2496 2995 3494 3993 4492 4992 5491 5990 6489 6988 7487; 8179 7712 9586 12893 10898 17757 17794 19202 20428 21491 35232];

scenario4Po0Java = [4992 5990 6988 7986 8984 9984 10982 11980 12978 13976 14974; 10437 23987 18065 23797 46086 70464 46007 59362 68008 44324 60781];
scenario4Po1Java = [4992 5990 6988 7986 8984 9984 10982 11980 12978 13976 14974; 15634 14085 18386 12716 47495 43332 56163 114481 36899 55043 120956];
scenario4Po10Java = [4992 5990 6988 7986 8984 9984 10982 11980 12978 13976 14974; 18271 9690 10189 31050 45205 31155 34542 79012 50209 68343 175347];
scenario4Po30Java = [4992 5990 6988 7986 8984 9984 10982 11980 12978 13976 14974; 9772 14942 17876 14697 41462 74772 56378 56269 45098 84141 102241];

scenario5Cw1Ps1Java = [3996 4795 5594 6393 7192 7992 8791 9590 10389 11188 11987; 3771 4545 5296 6027 6859 8116 8245 9385 10822 11472 12152];
scenario5Cw1Ps10Java = [3996 4795 5594 6393 7192 7992 8791 9590 10389 11188 11987; 3817 4626 5363 6068 6954 7662 8514 10109 10708 11429 12636];
scenario5Cw5Ps1Java = [3996 4795 5594 6393 7192 7992 8791 9590 10389 11188 11987; 5103 6334 10841 7704 8732 16306 23693 14009 14811 17217 19100];
scenario5Cw5Ps10Java = [3996 4795 5594 6393 7192 7992 8791 9590 10389 11188 11987; 5750 10373 11621 13154 13638 34194 27287 23193 35034 40047 46329];
scenario5Cw10Ps1Java = [3996 4795 5594 6393 7192 7992 8791 9590 10389 11188 11987; 5717 15217 12992 14263 18929 22252 28396 45849 39550 26797 82284];
scenario5Cw10Ps10Java = [3996 4795 5594 6393 7192 7992 8791 9590 10389 11188 11987; 13753 21234 28024 52928 74844 82785 181042 193160 238668 271568 354378];

%% COMPARISON

figure
hold on
plot(default(1,:), default(2,:), 'b')
plot(defaultJava(1,:), defaultJava(2,:), 'r')
title('Default')
xlabel('Number of requests')
ylabel('Response time (ms)')
legend('C++', 'Java')
hold off

figure
hold on
plot(scenario1Pmax10(1,:), scenario1Pmax10(2,:), 'b')
plot(scenario1Pmax10Java(1,:), scenario1Pmax10Java(2,:), 'r')
title('Scenario 1 - Pmax = 10')
xlabel('Number of requests')
ylabel('Response time (ms)')
legend('C++', 'Java')
hold off

figure
hold on
plot(scenario1Pmax15(1,:), scenario1Pmax15(2,:), 'b')
plot(scenario1Pmax15Java(1,:), scenario1Pmax15Java(2,:), 'r')
title('Scenario 1 - Pmax = 15')
xlabel('Number of requests')
ylabel('Response time (ms)')
legend('C++', 'Java')
hold off

figure
hold on
plot(scenario1Pmax20(1,:), scenario1Pmax20(2,:), 'b')
plot(scenario1Pmax20Java(1,:), scenario1Pmax20Java(2,:), 'r')
title('Scenario 1 - Pmax = 20')
xlabel('Number of requests')
ylabel('Response time (ms)')
legend('C++', 'Java')
hold off

figure
hold on
plot(scenario2Pmax10(1,:), scenario2Pmax10(2,:), 'b')
plot(scenario2Pmax10Java(1,:), scenario2Pmax10Java(2,:), 'r')
title('Scenario 2 - Pmax = 10')
xlabel('Number of requests')
ylabel('Response time (ms)')
legend('C++', 'Java')
hold off

figure
hold on
plot(scenario2Pmax15(1,:), scenario2Pmax15(2,:), 'b')
plot(scenario2Pmax15Java(1,:), scenario2Pmax15Java(2,:), 'r')
title('Scenario 2 - Pmax = 15')
xlabel('Number of requests')
ylabel('Response time (ms)')
legend('C++', 'Java')
hold off

figure
hold on
plot(scenario2Pmax20(1,:), scenario2Pmax20(2,:), 'b')
plot(scenario2Pmax20Java(1,:), scenario2Pmax20Java(2,:), 'r')
title('Scenario 2 - Pmax = 20')
xlabel('Number of requests')
ylabel('Response time (ms)')
legend('C++', 'Java')
hold off

figure
hold on
plot(scenario3Pmax5(1,:), scenario3Pmax5(2,:), 'b')
plot(scenario3Pmax5Java(1,:), scenario3Pmax5Java(2,:), 'r')
title('Scenario 3 - Pmax = 5')
xlabel('Number of requests')
ylabel('Response time (ms)')
legend('C++', 'Java')
hold off

figure
hold on
plot(scenario3Pmax10(1,:), scenario3Pmax10(2,:), 'b')
plot(scenario3Pmax10Java(1,:), scenario3Pmax10Java(2,:), 'r')
title('Scenario 3 - Pmax = 10')
xlabel('Number of requests')
ylabel('Response time (ms)')
legend('C++', 'Java')
hold off

figure
hold on
plot(scenario3Pmax15(1,:), scenario3Pmax15(2,:), 'b')
plot(scenario3Pmax15Java(1,:), scenario3Pmax15Java(2,:), 'r')
title('Scenario 3 - Pmax = 15')
xlabel('Number of requests')
ylabel('Response time (ms)')
legend('C++', 'Java')
hold off

figure
hold on
plot(scenario3Pmax20(1,:), scenario3Pmax20(2,:), 'b')
plot(scenario3Pmax20Java(1,:), scenario3Pmax20Java(2,:), 'r')
title('Scenario 3 - Pmax = 20')
xlabel('Number of requests')
ylabel('Response time (ms)')
legend('C++', 'Java')
hold off

figure
hold on
plot(scenario4Po0(1,:), scenario4Po0(2,:), 'b')
plot(scenario4Po0Java(1,:), scenario4Po0Java(2,:), 'r')
title('Scenario 4 - Po = 0')
xlabel('Number of requests')
ylabel('Response time (ms)')
legend('C++', 'Java')
hold off

figure
hold on
plot(scenario4Po1(1,:), scenario4Po1(2,:), 'b')
plot(scenario4Po1Java(1,:), scenario4Po1Java(2,:), 'r')
title('Scenario 4 - Po = 1')
xlabel('Number of requests')
ylabel('Response time (ms)')
legend('C++', 'Java')
hold off

figure
hold on
plot(scenario4Po10(1,:), scenario4Po10(2,:), 'b')
plot(scenario4Po10Java(1,:), scenario4Po10Java(2,:), 'r')
title('Scenario 4 - Po = 10')
xlabel('Number of requests')
ylabel('Response time (ms)')
legend('C++', 'Java')
hold off

figure
hold on
plot(scenario4Po30(1,:), scenario4Po30(2,:), 'b')
plot(scenario4Po30Java(1,:), scenario4Po30Java(2,:), 'r')
title('Scenario 4 - Po = 30')
xlabel('Number of requests')
ylabel('Response time (ms)')
legend('C++', 'Java')
hold off

figure
hold on
plot(scenario5Cw1Ps1(1,:), scenario5Cw1Ps1(2,:), 'b')
plot(scenario5Cw1Ps1Java(1,:), scenario5Cw1Ps1Java(2,:), 'r')
title('Scenario 5 - Cw = 1, Ps = 1')
xlabel('Number of requests')
ylabel('Response time (ms)')
legend('C++', 'Java')
hold off

figure
hold on
plot(scenario5Cw1Ps10(1,:), scenario5Cw1Ps10(2,:), 'b')
plot(scenario5Cw1Ps10Java(1,:), scenario5Cw1Ps10Java(2,:), 'r')
title('Scenario 5 - Cw = 1, Ps = 10')
xlabel('Number of requests')
ylabel('Response time (ms)')
legend('C++', 'Java')
hold off

figure
hold on
plot(scenario5Cw5Ps1(1,:), scenario5Cw5Ps1(2,:), 'b')
plot(scenario5Cw5Ps1Java(1,:), scenario5Cw5Ps1Java(2,:), 'r')
title('Scenario 5 - Cw = 5, Ps = 1')
xlabel('Number of requests')
ylabel('Response time (ms)')
legend('C++', 'Java')
hold off

figure
hold on
plot(scenario5Cw5Ps10(1,:), scenario5Cw5Ps10(2,:), 'b')
plot(scenario5Cw5Ps10Java(1,:), scenario5Cw5Ps10Java(2,:), 'r')
title('Scenario 5 - Cw = 5, Ps = 10')
xlabel('Number of requests')
ylabel('Response time (ms)')
legend('C++', 'Java')
hold off

figure
hold on
plot(scenario5Cw10Ps1(1,:), scenario5Cw10Ps1(2,:), 'b')
plot(scenario5Cw10Ps1Java(1,:), scenario5Cw10Ps1Java(2,:), 'r')
title('Scenario 5 - Cw = 10, Ps = 1')
xlabel('Number of requests')
ylabel('Response time (ms)')
legend('C++', 'Java')
hold off

figure
hold on
plot(scenario5Cw10Ps10(1,:), scenario5Cw10Ps10(2,:), 'b')
plot(scenario5Cw10Ps10Java(1,:), scenario5Cw10Ps10Java(2,:), 'r')
title('Scenario 5 - Cw = 10, Ps = 10')
xlabel('Number of requests')
ylabel('Response time (ms)')
legend('C++', 'Java')
hold off