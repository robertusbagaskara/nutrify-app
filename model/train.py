#script untuk folder test sama train
import glob, os
f= open("train.txt","w+")
f1= open("test.txt","w+")
directory = "bangkit/"
for lists in os.listdir(directory):
    for list in os.listdir(directory+lists):
        #print(list)
        if list == 'train':
            #print("Ini list data train")
            for root, dirs, files in os.walk(directory+lists+"/"+list):
                for file in files:
                    if file.endswith(".jpg"):
                        #print(directory+lists+"/"+list+"/"+file)
                        f.write(directory+lists+"/"+list+"/"+file+"\n")
        elif list == 'test':
            #print("Ini list data test")
            for root, dirs, files in os.walk(directory+lists+"/"+list):
                for file in files:
                    if file.endswith(".jpg"):
                        #print(directory+lists+"/"+list+"/"+file)
                        f1.write(directory+lists+"/"+list+"/"+file+"\n")
f.close()
f1.close()
