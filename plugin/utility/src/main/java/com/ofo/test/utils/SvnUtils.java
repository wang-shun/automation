package com.ofo.test.utils;

import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.util.jna.SVNJNAUtil;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNPersistentAuthenticationProvider;
import org.tmatesoft.svn.core.internal.wc.SVNWCProperties;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.*;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class SvnUtils {

    private String username;

    private String password;

    private String svnPath;

    private SVNURL svnUrl;

    private File realmCacheFile;

    private SVNClientManager clientManager;

    static {
        HttpsURLConnection.setDefaultHostnameVerifier(
                new HostnameVerifier() {
                    public boolean verify(String hostname, SSLSession sslSession) {
                        return true;
                    }
                });
    }

    /**
     * 使用本方法后，可使用getStoreUserName  getStorePassword 获取本地存储的用户名密码
     * 然后调用initLoad 方法后即可使用
     *
     * @param svnPath
     * @throws Exception
     */
    public static SvnUtils newInstance(String svnPath) throws Exception {
        SvnUtils svn = new SvnUtils(svnPath);
        return svn;
    }

    public static SvnUtils newInstance(String svnPath,Boolean iswork) throws Exception {
        SvnUtils svn = new SvnUtils(svnPath,iswork);
        return svn;
    }

    public static SvnUtils newInstance(String svnPath, String username, String password) throws Exception {
        SvnUtils svn = new SvnUtils(svnPath);
        svn.setUsername(username);
        svn.setPassword(password);
        svn.initLoad();
        return svn;
    }

    public static SvnUtils newInstance(String svnPath, String username, String password,Boolean iswork) throws Exception {
        SvnUtils svn = new SvnUtils(svnPath,iswork);
        svn.setUsername(username);
        svn.setPassword(password);
        svn.initLoad();
        return svn;
    }

    private SvnUtils() {
    }

    private SvnUtils(String svnPath) throws Exception {
        this.svnPath = svnPath;
        // get svn url
       SVNWCClient client = SVNClientManager.newInstance().getWCClient();

        SVNInfo info = client.doInfo(new File(svnPath), SVNRevision.WORKING);
        svnUrl = info.getURL();
        // get realm cache file
        realmCacheFile = getRealmCacheFile();

    }


    private SvnUtils(String svnPath,Boolean isWorking) throws Exception {
        this.svnPath = svnPath;
        // get svn url
        SVNWCClient client = SVNClientManager.newInstance().getWCClient();
        if(isWorking) {
            SVNInfo info = client.doInfo(new File(svnPath), SVNRevision.WORKING);
            svnUrl = info.getURL();
            // get realm cache file
            realmCacheFile = getRealmCacheFile();
        }else {
            svnUrl = SVNURL.parseURIEncoded(svnPath);
            realmCacheFile = getRealmCacheFile();
        }
    }



    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSvnUrlStr() {
        return svnUrl.toString();
    }

    public SVNURL getSvnUrl() {
        return svnUrl;
    }

    public void initLoad() throws Exception {
        clientManager = createClientManager(svnUrl, username, password);
    }

    public static SVNClientManager createClientManager(SVNURL svnUrl, String username, String password) throws SVNException {
        DAVRepositoryFactory.setup();
        SVNRepository repository = SVNRepositoryFactory.create(svnUrl);
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password);
        repository.setAuthenticationManager(authManager);
        DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
        return SVNClientManager.newInstance(options, authManager);
    }

    public static SVNRepository AuthRepository(SVNURL svnUrl, String username, String password) throws SVNException {
        SVNRepository svnRepository = SVNRepositoryFactory.create(svnUrl);
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password);
        svnRepository.setAuthenticationManager(authManager);
        return svnRepository;
    }

    public void update() throws Exception {
        SVNUpdateClient updateClient = clientManager.getUpdateClient();
        updateClient.setIgnoreExternals(false);
        DefaultSVNOptions opts = (DefaultSVNOptions) updateClient.getOptions();
        opts.setConflictHandler(new ISVNConflictHandler() {
            public SVNConflictResult handleConflict(SVNConflictDescription conflictDescription) throws SVNException {
                throw new SVNException(SVNErrorMessage.UNKNOWN_ERROR_MESSAGE);
            }
        });
        updateClient.doUpdate(new File(svnPath), SVNRevision.HEAD, SVNDepth.INFINITY,
                false, false);
    }

    public static void checkout(SVNURL svnUrl, String svnPath, String username, String password) throws Exception {
        SVNUpdateClient updateClient = createClientManager(svnUrl, username, password).getUpdateClient();
        updateClient.setIgnoreExternals(false);
        DefaultSVNOptions opts = (DefaultSVNOptions) updateClient.getOptions();
        opts.setConflictHandler(new ISVNConflictHandler() {
            public SVNConflictResult handleConflict(SVNConflictDescription conflictDescription) throws SVNException {
                throw new SVNException(SVNErrorMessage.UNKNOWN_ERROR_MESSAGE);
            }
        });


        updateClient.doCheckout(svnUrl, new File(svnPath), SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY, false);
    }

    public static void export(SVNURL svnUrl, String svnPath, String username, String password) throws Exception {
        SVNUpdateClient updateClient = createClientManager(svnUrl, username, password).getUpdateClient();
        updateClient.setIgnoreExternals(false);
        updateClient.doExport(svnUrl,new File(svnPath),SVNRevision.HEAD,SVNRevision.HEAD,"",false,SVNDepth.INFINITY);
    }

    public void commit(String message) throws Exception {
        SVNCommitClient commitClient = clientManager.getCommitClient();
        commitClient.doCommit(new File[]{new File(svnPath)}, true,
                message, null, null, false, false, SVNDepth.INFINITY);
    }

    public void delete(File destPath) throws Exception {
        SVNWCClient wcClient = clientManager.getWCClient();
        wcClient.doDelete(destPath, true, true, false);
    }

    public void add(File destPath) throws Exception {
        SVNWCClient wcClient = clientManager.getWCClient();
        wcClient.doAdd(new File[]{destPath}, true,
                false, false, SVNDepth.INFINITY, false, false, true);
    }

    public String getStoreUserName() throws Exception {
        if (realmCacheFile == null || !realmCacheFile.exists()) {
            return null;
        }
        SVNWCProperties properties = new SVNWCProperties(realmCacheFile, null);
        SVNProperties realmCache = properties.asMap();
        return SVNPropertyValue.getPropertyAsString(realmCache.getSVNPropertyValue("username"));
    }

    public String getStorePassword() throws Exception {
        if (realmCacheFile == null || !realmCacheFile.exists()) {
            return null;
        }
        SVNWCProperties properties = new SVNWCProperties(realmCacheFile, null);
        SVNProperties realmCache = properties.asMap();
        String userName = SVNPropertyValue.getPropertyAsString(realmCache.getSVNPropertyValue("username"));
        String realm = SVNPropertyValue.getPropertyAsString(realmCache.getSVNPropertyValue("svn:realmstring"));
        String passType = SVNPropertyValue.getPropertyAsString(realmCache.getSVNPropertyValue("passtype"));
        if (passType.equals(DefaultSVNPersistentAuthenticationProvider.SIMPLE_PASSTYPE)) {
            return SVNPropertyValue.getPropertyAsString(realmCache.getSVNPropertyValue("password"));
        } else if (passType.equals(DefaultSVNPersistentAuthenticationProvider.WIN_CRYPT_PASSTYPE)) {
            return SVNJNAUtil.decrypt(SVNPropertyValue.getPropertyAsString(realmCache.getSVNPropertyValue("password")));
        } else if (passType.equals(DefaultSVNPersistentAuthenticationProvider.MAC_OS_KEYCHAIN_PASSTYPE)) {
            return SVNJNAUtil.getPasswordFromMacOsKeychain(realm, userName, true);
        } else if (passType.equals(DefaultSVNPersistentAuthenticationProvider.GNOME_KEYRING_PASSTYPE)) {
            return SVNJNAUtil.getPasswordFromGnomeKeyring(realm, userName, true, null);
        } else {
            return null;
        }
    }

    private File getRealmCacheFile() throws Exception {
        File configDirectory = SVNWCUtil.getDefaultConfigurationDirectory();
        File authCacheArea = new File(configDirectory, "auth");
        File authPath = new File(authCacheArea, "svn.simple");
        String svnFileName = getRealm(authPath);
        if (svnFileName == null)
            return null;
        else
            return new File(authPath, svnFileName);
    }

    private String getRealm(File authPath) throws IOException {
        String filePath = null;
        for (File file : authPath.listFiles()) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(file));
                String line = br.readLine();
                while (line != null) {
                    if (line.contains(this.svnUrl.getHost())) {
                        filePath = file.getName();
                        break;
                    }
                    line = br.readLine();
                }
            } catch (IOException ex) {

            } finally {
                if (br != null)
                    br.close();
            }

            if (filePath != null)
                break;
        }

        return filePath;
    }

    public static List<String> getEntryNameByUrl(String svnUrl, String relativePath, String username, String password) throws SVNException {
        SVNRepository svnRepository = AuthRepository(SVNURL.parseURIEncoded(svnUrl), username, password);
        Collection collection = svnRepository.getDir(relativePath, -1, null, (Collection) null);
        List<String> entryNameList =  new ArrayList<String>();
        Iterator iterator = collection.iterator();
        SVNDirEntry svnDirEntry;
        while (iterator.hasNext()) {
            svnDirEntry = (SVNDirEntry) iterator.next();
            entryNameList.add(svnDirEntry.getName());
        }
        return entryNameList;
    }

    public static String getSvnFileContentByUrl(String svnUrl, String relativePath, String username, String password) throws SVNException, IOException {
        SVNRepository svnRepository = AuthRepository(SVNURL.parseURIEncoded(svnUrl), username, password);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        svnRepository.getFile(relativePath, -1, null, outputStream);
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
        resultStream.write(outputStream.toByteArray());
        return resultStream.toString();
    }


    public boolean isURLExist(SVNURL svnUrl,String username, String password) {
        try {
            SVNRepository svnRepository = SVNRepositoryFactory.create(svnUrl);
            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password);
            svnRepository.setAuthenticationManager(authManager);
            SVNNodeKind nodeKind = svnRepository.checkPath("", -1);
            return nodeKind == SVNNodeKind.NONE ? false : true;
        } catch (SVNException e) {
            e.printStackTrace();
        }
        return false;
    }



    public void Import(File file, String message) throws Exception {


        SVNCommitClient commitClient = clientManager.getCommitClient();
        //commitClient.doImport(file,svnUrl,message,null,false,false,SVNDepth.INFINITY,false);
        Boolean isexits = isURLExist(svnUrl, username, password);
        if(isexits) {
            SVNURL[] svnurls={svnUrl};
            commitClient.doDelete(svnurls, "重复删除文件");
        }
        commitClient.doImport(file, svnUrl, message, null, true, true, null);


    }

    public static void main(String[] a) throws Exception {

        URL url = new URL("https://repo.ds.gome.com.cn:8443/svn/test/LoadTest/OrderGroup/cartLoadFrameConf/ExecuteTest/uat-lib/JMeterPlugins-Standard.jar");

        export(SVNURL.create(url.getProtocol(), "", url.getHost(), url.getPort(), url.getPath(), true), "/Users/zhangjiadi/Documents/Maven/JMeterPlugins-Standard.jar", "zhangjiadi", "di123");
        System.out.println("ok!pk!");
    }


}
