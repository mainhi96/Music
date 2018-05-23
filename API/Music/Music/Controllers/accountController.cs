using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;

namespace Music.Controller
{
    public class accountController : ApiController
    {
        [HttpGet]
        public List<account> GetAccLists()
        {
           DBMusicDataContext db = new DBMusicDataContext();
            return db.accounts.ToList();
        }
///
 
[HttpGet]
        public account GetAcc(String username)
        {
            DBMusicDataContext db = new DBMusicDataContext();
            return db.accounts.FirstOrDefault(x => x.username == username);
        }
        ///


        [HttpPost]
        public bool InsertNewAcc(string username, string password)
        {
            try
            {
                DBMusicDataContext db = new DBMusicDataContext();
                account acc = new account();
                acc.username = username;
                acc.password = password;
                db.accounts.InsertOnSubmit(acc);
                db.SubmitChanges();
                return true;
            }
            catch
            {
                return false;
            }
        }


        [HttpPut]
        public bool ChangePass(String name, String pass)
        {
            try
            {
                DBMusicDataContext db = new DBMusicDataContext();
                account acc = db.accounts.FirstOrDefault(x => x.username == name);
                if (acc == null) return false;//không tồn tại false
                acc.password = pass;
                db.SubmitChanges();//xác nhận chỉnh sửa
                return true;
            }
            catch
            {
                return false;
            }
        }
///
 

[HttpDelete]
        public bool DeleteFood(String name)
        {
            DBMusicDataContext db = new DBMusicDataContext();
            account acc = db.accounts.FirstOrDefault(x => x.username == name);
            if (acc == null) return false;//không tồn tại false
            db.accounts.DeleteOnSubmit(acc);
            db.SubmitChanges();
            return true;
        }
    }
}
