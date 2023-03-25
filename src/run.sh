for i in $(seq 1 1000)
do
     echo -e "\nRun n.º " $i
     d:; cd 'd:\repos\SD_Project1'; & 'C:\Program Files\Java\jdk-14.0.2\bin\java.exe' '-XX:+ShowCodeDetailsInExceptionMessages' '-cp' 'C:\Users\diogo\AppData\Roaming\Code\User\workspaceStorage\ffecfd6c13ae3a26996d824a56115ea4\redhat.java\jdt_ws\SD_Project1_e593df5f\bin' 'src.HeistToTheMuseum' 
done
