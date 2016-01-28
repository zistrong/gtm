#!/bin/bash
. ~/.profile

#####################################

#zhananns
#desc：将经分生成的消费分析数据入库

#author：zhangziqiang

#date：2015-8-13 09:28:22

#####################################
ftp_ip=
ftp_user=billapp
ftp_passwd=
oracle_sid=cb/cb0120


#上个月
getLastMonth() {

month=`date +%m`
year=`date +%Y`
month=`expr $month - 1`

if [ $month -lt 0 ]
then
month=12
year=`expr $year - 1`
fi

if [ $month -lt 10 ]
then
month=0$month
fi
echo $year$month

}


#从ftp下载的函数
download() {


ftp -ivn $ftp_ip 21 << EOF>>ftp.log
user $ftp_user $ftp_passwd
cd   /etldata4/billapp/91927
ascii
prompt off
mget $1
mdel $1
bye
EOF


}



data2db() {

download $1

cat <<EOF>$1.ctl

load
infile "$1"   
append into table T_CB_CAINFO
fields terminated by "|"  
trailing nullcols       
(
STATIS_MONTH,
SERV_NUMBER,
USER_ID,   
JIAOFEI_FEE_TOTAL ,
BASE_CALL_DURATION_TOTAL,
ROAM_CALL_DURATION_TOTAL ,
SEND_10086_CNT_TOTAL ,
SEND_SMS_CNT_TOTAL ,
CHANGE_CL_CNT_TOTAL ,
JIAOFEI_FEE_AVG ,
BASE_CALL_DURATION_AVG ,
ROAM_CALL_DURATION_AVG,
SEND_10086_CNT_AVG ,
SEND_SMS_CNT_AVG ,
CHANGE_CL_CNT_AVG ,
SMS_MONTH1,
SMS_MONTH2,
SMS_MONTH3,
CALL_DURATION_GN_1,
GUONEI_GPRS_1,
CALL_DURATION_GN_2,
CALL_DURATION_GN_3,
GUONEI_GPRS_2,
GUONEI_GPRS_3,
CALL_DURATION_ALL_1,
CALL_DURATION_ALL_2,
CALL_DURATION_ALL_3,
ALL_GPRS_1,
ALL_GPRS_2,
ALL_GPRS_3,
PARTITION_ID "MOD(:USER_ID,10000)"
) 
EOF

sqlldr $oracle_sid  control=$1.ctl direct=true streamsize=104857600 date_cache=10000 log=log/$1.log
rm $1.ctl
compress $1 
mv $1.Z bak


}

#清空表
truncatetable() {
touch null.txt
cat <<EOF>null.ctl

load
infile "null.txt"   
truncate into table T_CB_CAINFO
fields terminated by "|"  
trailing nullcols       
(
USER_ID
) 
EOF

sqlldr $oracle_sid  control=null.ctl direct=true streamsize=104857600 date_cache=10000
rm null.ctl
rm null.txt
rm null.log

}
echo `date`
cd ~/itf/91927
lastMonth=`getLastMonth`
okfile=M91927${lastMonth}00.OK
okfile=M9192720150500.OK
#下载OK文件
download $okfile

if [ -e $okfile ] 
then
truncatetable

echo 开始下载AVL文件

filelist=`sed -e '1d'  $okfile`
for loop in $filelist
do

data2db $loop 

done

mv $okfile bak

fi


echo `date`



