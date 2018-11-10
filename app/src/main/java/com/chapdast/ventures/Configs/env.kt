package com.chapdast.ventures.Configs

import com.google.firebase.analytics.FirebaseAnalytics

/**
 * Created by pejman on 5/25/18.
 */
//Auto SMS READ
const val AUTO_READ = true

//Auto Code Confirm
const val AUTO_CONFIRM = false


//Time Limit Of Number Of Quest
const val QUEST_TIME_LIMIT = 60*60*24

// Time Limit For request New Code on Same Number
const val TIME_FOR_NEW_CODE = 5*60

const val PAY_TAG = "mk-pay"
//Main Server ADDRESS

const val SERVER_ADDRESS = "https://www.hellogram.app/EnglishApp/sendrequest-v2.php"
const val VPN_CHECK_ADDRESS = "https://www.hellogram.app/EnglishApp/vpn.php"
const val MEDIA_SERVER_ADDRESS = "https://www.hellogram.app/EnglishApp/"

//Support Creds

const val SUPP_TELL = "02154872306"
const val SUPP_EMAIL = "info@aban.mobi"


//Analytics Server Address

const val ANA_SERVER = "https://www.hellogram.app/EnglishApp/ana/ana.php"

const val SMS_LINE_NO = "98308240"

// yandex Translate
const val TRANS_SERVER_ADDRESS="https://translate.yandex.net/api/v1.5/tr.json/translate"
const val TRANS_KEY = "trnsl.1.1.20180525T130433Z.43cc22f3a3e31cf2.e5e668ce174e2d2a6f63393fd889285fed6578e4"

const val DESC_TRANS = "https://owlbot.info/api/v2/dictionary/"

const val RSA_KEY="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCAOb9udhWv2o57oSCFj/Nh+SV20Fex2RuWVw0Dz4yinJQ/4RmvnKjskmWy1Y9xD9IEUEa4aa8RlD9zC1ebjGY9DcBH1TfpHFWMI7B3+ddjk2xrdnDOpHYzU/8umUG1SAujP1bbm6xHXLLVwqd5xKAVBv6h6LphDFXTQS7fgPITEwIDAQAB"
const val SUK_KEY = "ABAHEL30"

var smsCode=-1
var gotSms=false
const val smsAct="mk.com.chapdast.ventures.smscode"

// REQUEST CODES
const val STRG_REQUEST_CODE = 8600
const val CAT_STRG_REQUEST_CODE = 8601
const val SMS_REC_CODE = 9880
const val IMEI_CODE = 9881

var FIREBASE_CLI :FirebaseAnalytics?=null
