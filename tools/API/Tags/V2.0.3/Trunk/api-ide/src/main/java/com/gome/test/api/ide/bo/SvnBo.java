package com.gome.test.api.ide.bo;

import java.io.*;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNPropertyValue;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.util.jna.SVNJNAUtil;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNPersistentAuthenticationProvider;
import org.tmatesoft.svn.core.internal.wc.SVNFileUtil;
import org.tmatesoft.svn.core.internal.wc.SVNWCProperties;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.ISVNConflictHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;
import org.tmatesoft.svn.core.wc.SVNConflictDescription;
import org.tmatesoft.svn.core.wc.SVNConflictResult;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class SvnBo {

    @Value(value = "${svn.username}")
    private String username;

    @Value(value = "${svn.password}")
    private String password;

    private final String svnPath;

    private final SVNURL svnUrl;

    private final File realmCacheFile;

    private SVNClientManager clientManager;

    static {
        HttpsURLConnection.setDefaultHostnameVerifier(
                new HostnameVerifier() {
                    public boolean verify(String hostname, SSLSession sslSession) {
                        return true;
                    }
                });
    }

    public SvnBo(String svnPath) throws Exception {
        this.svnPath = svnPath;
        // get svn url
        SVNWCClient client = SVNClientManager.newInstance().getWCClient();
        SVNInfo info = client.doInfo(new File(svnPath), SVNRevision.WORKING);
        svnUrl = info.getURL();
        // get realm cache file
        realmCacheFile = getRealmCacheFile();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSvnUrl() {
        return svnUrl.toString();
    }

    public void initLoad() throws Exception {
        // get svn client manager

        DAVRepositoryFactory.setup();
        SVNRepository repository = SVNRepositoryFactory.create(svnUrl);
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password);
        repository.setAuthenticationManager(authManager);
        DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
        clientManager = SVNClientManager.newInstance(options, authManager);
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

    public void commit() throws Exception {
        String message = String.format("非交互模式提交所有修改");
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
}
