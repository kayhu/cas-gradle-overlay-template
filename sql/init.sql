create database kauth;

use kauth;

insert into tenant (code, name, description, disabled, create_time, update_time) VALUES ('iakuh', 'IAKUH', null, false, now(), now());
select @tenant_id:=id from tenant where code='iakuh';
insert into domain (code, name, description, tenant_id, public_key, private_key, disabled, create_time, update_time) VALUES ('skeleton', 'SKELETON', null, @tenant_id, 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCWfbt2YZ95UpJ210ipRK5ADf0WfxvsBTD4EzewR09+s0CgwNNSj7Z1Pj+UAO9xNJNgLRpEA6tO0OD5yIFpHwtKMn06GLTCrkRNGXqeqQ690I4jjR5ODcBDfIDBUzx/6cl11dZg9C3+53GKcGU9+3aETWStovycWdZm3rTtDGPdMwIDAQAB', 'MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJZ9u3Zhn3lSknbXSKlErkAN/RZ/G+wFMPgTN7BHT36zQKDA01KPtnU+P5QA73E0k2AtGkQDq07Q4PnIgWkfC0oyfToYtMKuRE0Zep6pDr3QjiONHk4NwEN8gMFTPH/pyXXV1mD0Lf7ncYpwZT37doRNZK2i/JxZ1mbetO0MY90zAgMBAAECgYBUATVunInvqFcXvY7S5gK6wNRBBY0pj7BJGjkgzn2ihR8TXO/Zi11XTvsge/Es6SNHMYYS51Vt9o1PAUyzyF99FmxwDuKBsv/Wbr3yC4bAyvOTzdFiMDY4NCcZk3VdooSdCsJaspm2uOfnnYUvEZCcabSEmr9k3bb40r1h5pNG+QJBAPwp532yUqAMqh4YA7dyW1Gh37QjJMtjkvOXF8iWyN7WZQcrDarPfVQmUrsXUQa6P1KkNRApAr/FXDVJ7XUUh+8CQQCYx9iwFTkB5cCmBgaqFoIEETN4UcI70PgJHCyXm4229AxlZhYHPoRbLJd2g8Flg5SefIxeULApc8wxz3RCKNr9AkAqmuXq/fMp6ZngpcwS9bZqL4B9jiaMWtGcaJ3zHU7pH65ILMUNZCtXyXXW+JSPH27NhsMUbOly/2SZ+FivnH1vAkATGhmLmnuwMwutNl+q8Hl9DLGEv6QrWmtIqE8i/X3we/74xpTGfxiJVb/yP3L0wEjar/PU7v23kRTcvXVpuDX5AkEAqHWfzvxZmvM7b3qkKRY22i9YLp7QLTCq9PySIqBkSaatCqhtxWK0tzFwEtXyCCkCwMi4GzJVgrgnNWj0/qI/mQ==', false, now(), now());
select @domain_id:=id from domain where code='skeleton' and tenant_id=@tenant_id;
insert into user (username, password, tenant_id, disabled, create_time, update_time) values ('huk', '$2a$10$twyJaPcoE2EhvQo4x2KbqOhcrK8hZgkVPkNlIQTV2pRx3JamFzwAi', @tenant_id, false, now(), now());
insert into role (code, name, description, domain_id, disabled, create_time, update_time) values ('ROLE_ADMIN', 'Administrator', '管理员', @domain_id, false, now(), now());
select @user_id:=(select id from user where username='huk' and tenant_id=@tenant_id);
select @role_id:=(select id from role where code='ROLE_ADMIN' and domain_id=@domain_id);
insert into ref_user_role (user_id, role_id) values (@user_id, @role_id);
insert into token (token, domain_id, aud, exp, iat, create_time, update_time) values ('ZDg0OTA2YzA0ZmI0NGY5MjkyNDNhNTY0NGYwMDViYWY', @domain_id, 'https://skeleton.iakuh.org', 1842453736866, 1527093736866, now(), now());