package com.example.bigproject;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import com.scottyab.aescrypt.AESCrypt;
import com.yakivmospan.scytale.Crypto;
import com.yakivmospan.scytale.KeyProps;
import com.yakivmospan.scytale.Options;
import com.yakivmospan.scytale.Store;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.util.Calendar;
import java.util.Properties;
import java.util.Vector;

import javax.crypto.SecretKey;
import javax.security.auth.x500.X500Principal;





public class LocalBase {

    private static Context context;
    private static String root = "";
    private static String folderForZametka = "";
    private static String folderForImages = "";
    private static String storeName = "";
    private static String storePassword = "";
    private static String alias = "";
    private static String keyPairPassword = "";


                    /* Инициализация важных переменных из config.propirties */
    public static void initialization(Context contextIn)
    {
        context = contextIn;
        root = String.valueOf(context.getFilesDir());
        Properties properties = new Properties();
        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open("config.properties");
            properties.load(inputStream);
            folderForZametka = properties.getProperty("folderForZametka");
            folderForImages = properties.getProperty("folderForImages");

            storeName = properties.getProperty("storeName");
            alias= properties.getProperty("alias");
            storePassword = properties.getProperty("storePassword");
            keyPairPassword = properties.getProperty("keyPairPassword");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

                    /* Когда заходит неавторизованный пользователь */
    private static void makeKeys()
    {

        final Calendar start = Calendar.getInstance();
        final Calendar end = Calendar.getInstance();
        end.add(Calendar.YEAR, 100);


        KeyProps keyProps = new KeyProps.Builder()
                .setAlias(alias)
                .setPassword(keyPairPassword.toCharArray())
                .setKeySize(256)
                .setKeyType(Options.ALGORITHM_AES)
                .setBlockModes(Options.BLOCK_MODE_CBC)
                .setEncryptionPaddings(Options.PADDING_PKCS_7)
                .build();
        Store store = new Store(context,storeName,storePassword.toCharArray());
        store.generateSymmetricKey(alias,keyPairPassword.toCharArray());
    }
    public static void firstSetting()
    {
        //Создаём папку для хранения заметок
        File file1 = new File(root + folderForZametka);
        if (!file1.exists()) {
            file1.mkdir();
        }
        //Создаём папку для хранения изображений
        File file2 = new File(root + folderForImages);
        if (!file2.exists()) {
            file2.mkdir();
        }

        //Создаём ключи для шифрования
        makeKeys();
    }

                    /* Шифрование */
    public static synchronized String encode(String value)
    {
        Crypto crypto = new Crypto(Options.TRANSFORMATION_SYMMETRIC);

        Store store = new Store(context,storeName,storePassword.toCharArray());

        SecretKey secretKey = store.getSymmetricKey(alias,keyPairPassword.toCharArray());

        value = "Attachment parties pure sociable started extended at rejoiced. \n" +
                "\n" +
                "Speaking lively luckily lady mother himself attachment bred form been necessary. Think possible near. Order dashwoods might arranging unknown having burst sending. Interested remainder fruit. Unable absolute perpetual tedious give improving conveying taken attacks doors miles. \n" +
                "\n" +
                "Very rent head. Books friend behind tears. Necessary september prepared certainty fond up civil direction keeps. Judgment continuing rich laughter welcome eat fact remark same discovered. Speaking then tears private unlocked around desirous necessary witty remaining principles. \n" +
                "\n" +
                "Forming most furniture shortly daughters advanced worth. Unsatiable period insipidity attacks plan downs northward consider ask mistaken even few. Full ourselves well evening one northward offending stimulated thoughts added witty aware. Country event weather more right pleased you blush too forfeited interested point turned procured welcomed. Genius propriety mirth comfort smallest evening remember wound shy. \n" +
                "\n" +
                "Other merry marry explain properly attention mrs otherwise excuse. Exposed procured visitor daughter forbade known china arose wandered. Feet open may length strictly mirth we eldest warmly unreserved weather state domestic favour ignorant. Suitable looking perpetual use drift dinner. Danger talked lasted am humanity shyness relied effect believe adieus. \n" +
                "\n" +
                "Months widen still enabled margaret favourable towards man followed. True acuteness event rapid mother john spoil amounted smallest throwing admiration itself waited. Procuring equal unwilling fulfilled shy misery feet scarcely tastes hundred improve hastily. Cultivated married intention. Feebly little dissuade coming answered delay principle plan concealed past pleasure when read terms mistress. \n" +
                "\n" +
                "Unfeeling carried avoid remember justice change around change. Wicket departure welcomed game added forty get rapturous believe post engrossed. Thought suffer forming followed shameless regular three too solicitude. Horses favourable misery. But removing wonder gave child since cordial differed civil affronting whether thirty dine whole. \n" +
                "\n" +
                "Vicinity preference four amiable leave assurance china turned. Additions removing continue been vicinity week pulled eyes avoid while feeling extremely. Sudden respect abilities before become change quit breeding has additions musical delay message income pure hoped the. Sang produced believe county become more round event called unfeeling ashamed exposed outweigh think thought outlived what. Repair said likely make wandered chamber. \n" +
                "\n" +
                "Child desire numerous sell ten justice being sportsman nature country jokes father procuring related. Norland as early before answer. Family contempt highest village suspicion dwelling. Voice partiality cease improve times her sold dashwoods as way burst. Journey bed young tolerably door abilities. \n" +
                "\n" +
                "Fulfilled inquietude six address fact late call resolve beauty forth. Off blessing genius add. Wound warrant almost favour intention feebly behaved good replied agreeable happiness afraid. Pasture whatever downs arranging remarkably consider common pretty on cultivated use regular why shortly. Occasional sometimes song. \n" +
                "\n" +
                "Will estimating marriage style mother outward hoped nor continuing. Since amiable attempt assurance there piqued temper sir asked found if before. Differed departure what another pianoforte dearest sister. Weeks she linen middleton morning engage forth middleton same moment. Green removal savings procured venture feet desire. \n" +
                "\n" +
                "Together affixed stand gravity leaf easy told admire looked amongst being opinions principles friendship request. ";

        String enc = "";
        Bitmap bitmap;
        String password = "~=2UC:!y2NHJ(mh.-([fqZ\\)\"+W+9}Yt6</nBJjwLJ*BAMw-M\\vpQKbqP{u&Fs8E\"dDn]x>#)]}&=-*Kc-paJsVkRZwjAD][g=g*5fe}N>*J5AGe*Mpn{w4Q/+FFM%)@c-,+:BHWv7r>*D+gRmXJPh@3589s+5m.;8$2EBn2?az?LfH=G--D\\-!>k^73:k3=9bw{v8p9@3D%_N<4e'NWxxXdtaf9MH\"<[W]H;rP$Q{Jxujup\\9,c$*#)Tue6p[sC";

        String encryptedData = crypto.encrypt(value,secretKey,true);
        String DencryptedData = crypto.decrypt(encryptedData,secretKey);
        Bitmap bitmap1 = ZametkaWork.deSerializationBitmap(DencryptedData);
        return encryptedData;
    }
    public static synchronized String deCode(String value)
    {


        Crypto crypto = new Crypto(Options.TRANSFORMATION_SYMMETRIC);

        Store store = new Store(context,storeName,storePassword.toCharArray());
        SecretKey secretKey = store.getSymmetricKey(alias,keyPairPassword.toCharArray());

        String encryptedData = crypto.decrypt(value,secretKey,true);
        return encryptedData;

    }


                    /* Сереализация */
    private static synchronized String serializationZametke(Serializable serializable) throws IOException
    {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

                        objectOutputStream.writeObject(serializable);
                        objectOutputStream.close();

                        return Base64.encodeToString(byteArrayOutputStream.toByteArray(),0);
                    }
    public static synchronized Zametka deSerializationZametka(String str) throws IOException,ClassNotFoundException
    {
        byte[] data = Base64.decode(str,0);
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data));

        Zametka zametka = (Zametka) objectInputStream.readObject();
        objectInputStream.close();
        return zametka;
    }



                    /* Сохранение данныых на устройстве */
    //Сохраняем картинку в папку
    private static synchronized boolean saveStrBitmap(String name, String strBtm)
    {
        File file1 = new File(root + folderForImages + "/" + name + ".txt");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file1);

            String strBtmEncode = encode(strBtm);
            char[] arrS = deCode(strBtmEncode).toCharArray();
            String arr = deCode(strBtmEncode);
            Bitmap bitmap = ZametkaWork.deSerializationBitmap(String.valueOf(arrS));


            fileOutputStream.write(strBtmEncode.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    //Сохраняем заметку в папку
    public static synchronized boolean save(Zametka zametka)
    {

        if(saveStrBitmap(zametka.getData(),zametka.getBitmap())) {

            zametka.setBitmap("");

            //Имя файла = дата создания заметки
            File file1 = new File(root + folderForZametka +"/"+ zametka.getData() + ".txt");
            try {
                String zamStr = serializationZametke(zametka);
                String zamStrEnc = encode(zamStr);

                FileOutputStream fileOutputStream = new FileOutputStream(file1);
                fileOutputStream.write(zamStrEnc.getBytes());
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }else return false;

        return true;
    }



                    /* Получаем данные */
    //Получаем вектор,содержащий все заметки в памяти устройства. Название заметки и её картинки совпадают
    public static synchronized Vector<Zametka> getZamLocal() throws FileNotFoundException
    {
        Vector<Zametka> vector = new Vector<>();

        File folder = new File(root+folderForZametka);


        String[] listOfFiles;
        if(folder.list() == null) return null;
        else {
             listOfFiles = folder.list();
        }

        File file;
        StringBuilder stringBuilder = new StringBuilder();
        String len = "";
        for(int i=0;i<listOfFiles.length;i++){

            file = new File(root+folderForZametka+"/"+listOfFiles[i]);

            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

            try {
                stringBuilder.setLength(0);
                while (( len=bufferedReader.readLine() ) != null)
                    stringBuilder.append(len);

                vector.add(deSerializationZametka(deCode(String.valueOf(stringBuilder))));
                fileInputStream.close();

            }catch (IOException | ClassNotFoundException e){
                e.printStackTrace();
            }
        }

        return vector;
    }
    //Получаем картинку по её имени
    public static synchronized Bitmap getBitmap(String name) throws FileNotFoundException {
        File file = new File(root+folderForImages+"/"+name);
        Bitmap bitmap = null;
        StringBuilder stringBuilder = new StringBuilder();
        String len = "";
        FileInputStream fileInputStream = new FileInputStream(root+folderForImages+"/"+name);
        BufferedReader bufferedReader;
        String text="";

        try
        {
            byte[] bytes = new byte[fileInputStream.available()];
            fileInputStream.read(bytes);
            text = new String(bytes);
            fileInputStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return bitmap;
        }

        bitmap = ZametkaWork.deSerializationBitmap(deCode(text));
        return bitmap;
    }
}

