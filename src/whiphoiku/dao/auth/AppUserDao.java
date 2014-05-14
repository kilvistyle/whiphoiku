package whiphoiku.dao.auth;

import org.slim3.datastore.DaoBase;
import org.slim3.datastore.Datastore;

import whiphoiku.meta.auth.AppUserMeta;
import whiphoiku.model.auth.AppUser;

public class AppUserDao extends DaoBase<AppUser>{

	private static final AppUserMeta meta = AppUserMeta.get();
	
	public AppUser findByMail(String mail) {
		return Datastore.query(meta)
			.filter(meta.mail.equal(mail))
			.asSingle();
	}
	
}
