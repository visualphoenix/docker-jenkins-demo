import hudson.model.*
import jenkins.model.*

//Thread.start {
  def e = { filepath ->
    evaluate(new File('/jenkins/init.groovy.d/' + filepath))
  }
  def admin_email = e("./../init.groovy.mixins/AdminEmail.groovy")
  def agent_port = e("./../init.groovy.mixins/AgentPort.groovy")
  def chmod = e("./../init.groovy.mixins/Chmod.groovy")
  def csrf = e("./../init.groovy.mixins/Csrf.groovy")
  def extended_email = e("./../init.groovy.mixins/ExtendedEmail.groovy")
  def envvars = e("./../init.groovy.mixins/Envvars.groovy")
  def num_executors = e("./../init.groovy.mixins/NumExecutors.groovy")
  def git = e("./../init.groovy.mixins/Git.groovy")
  def java = e("./../init.groovy.mixins/Java.groovy")
  def ldap = e("./../init.groovy.mixins/Ldap.groovy")
  def mailer = e("./../init.groovy.mixins/Mailer.groovy")
  def master_slave_security = e("./../init.groovy.mixins/MasterSlaveSecurity.groovy")
  def matrix_authorization = e("./../init.groovy.mixins/MatrixAuthorization.groovy")
  def maven = e("./../init.groovy.mixins/Maven.groovy")
  def quiet_period = e("./../init.groovy.mixins/QuietPeriod.groovy")
  def set_user = e("./../init.groovy.mixins/SetUser.groovy")
  def ssh_credential = e("./../init.groovy.mixins/SshCredentials.groovy")
  def user_credential = e("./../init.groovy.mixins/UserCredentials.groovy")
  def vault_unseal = e("./../init.groovy.mixins/VaultUnseal.groovy")
//  //sleep 10000
  def env = System.getenv()
  def j = Jenkins.getInstance()
  def sonar_host = env['SONAR_HOST']
  def token = env["VAULT_TOKEN"]
  def vault_addr = env["VAULT_ADDR"]
  def home = env["JENKINS_HOME"]
  def creds = '''\
name,global_admin,global_configure_updatecenter,global_read,global_run_scripts,global_upload_plugins,credentials_create,credentials_delete,credentials_manage_domains,credentials_update,credentials_view,agent_build,agent_configure,agent_connect,agent_create,agent_delete,agent_disconnect,job_build,job_cancel,job_configure,job_create,job_delete,job_discover,job_read,job_workspace,run_delete,run_update,view_configure,view_create,view_delete,view_read,scm_tag,metrics_health_check,metrics_thread_dump,metrics_view,job_extendedread,job_move,view_replay
Anonymous,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
authenticated,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
billy,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
swarm,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
'''
  set_user(username='swarm', fullname='swarm', email='swarm@example.org')

  vault_unseal(token, vault_addr + '/v1/secret/jenkins/jenkins-ssh-private-key', home + '/.ssh/id_rsa')
  vault_unseal(token, vault_addr + '/v1/secret/jenkins/jenkins-ssh-public-key',  home + '/.ssh/id_rsa.pub')
  chmod("600", home + '/.ssh/id_rsa')
  chmod("600", home + '/.ssh/id_rsa.pub')

  admin_email(
    instance = j,
    admin_addr= env['JENKINS_ADMIN_ADDR']
  )
  agent_port(
    instance = j,
    port     = env["JENKINS_SLAVE_AGENT_PORT"].toInteger()
  )
  csrf(
    instance    = j,
    enable_csrf = true
  )
  extended_email(
    instance = j
  )
  envvars(
    instance    = j,
    env_var_map = [ "LANG": "en_US.UTF-8", "MAVEN_REPO_DIR": "/jenkins/.m2repos" ]
  )
  num_executors(
    instance      = j,
    num = env["JENKINS_EXECUTORS"].toInteger()
  )
  git(
    instance = j,
    name  = env['JENKINS_GIT_NAME'],
    email = env['JENKINS_GIT_EMAIL']
  )
  java(
    instance  = j,
    java_name = "Java",
    java_home = "/usr/lib/jvm/java-8-oracle"
  )
  ldap(
    instance         = j,
    ldap_addr        = env["LDAP_ADDR"],
    ldap_rootDN      = env["LDAP_ROOTDN"],
    ldap_managerDN   = env["LDAP_MANAGERDN"],
    ldap_managerPass = vault_unseal(token, vault_addr + '/v1/secret/jenkins/ldap-password', '-')
  )
  mailer(
    instance     = j,
    smtp_host    = env['SMTP_HOST'],
    replyto_addr = env['JENKINS_REPLYTO_ADDR'],
    email_suffix = env['JENKINS_EMAIL_SUFFIX']
  )
  master_slave_security(
    instance = j,
    home     = home,
    disabled = false
  )
  matrix_authorization(
    instance      = j,
    user_mappings = creds
  )
  maven(
    instance   = j,
    maven_home = '$MAVEN_HOME',
    maven_opts = '-Xmx1024m'
  )
  quiet_period(
    instance = j,
    period   = env["JENKINS_QUIET_PERIOD"].toInteger()
  )
  ssh_credential(
    instance = j,
    id = 'jenkins',
    username = 'jenkins',
    description = 'jenkins'
  )
  user_credential(
    instance = j,
    username = 'swarm',
    password = vault_unseal(token, vault_addr + '/v1/secret/jenkins/jenkins-swarm-api-key', '-')
  )
  instance.save()
//}
