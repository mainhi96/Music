using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;

namespace Music.Controller
{
    public class voteController : ApiController
    {
        [HttpGet]
        public List<comment> GetListComment(int songid)
        {
            DBMusicDataContext db = new DBMusicDataContext();
            return db.comments.Where(x => x.songid==songid).ToList();
        }

        [HttpGet]
        public List<vote> GetListvote(int songidv)
        {
            DBMusicDataContext db = new DBMusicDataContext();
            return db.votes.Where(x => x.songid == songidv).ToList();
        }
        [HttpGet]
        public double Point(int songidp)
        {
            DBMusicDataContext db = new DBMusicDataContext();
            List<vote> list= db.votes.Where(x => x.songid == songidp).ToList();
            double point=0;
            foreach(vote v in list)
            {
                point = point + v.point;

            }
            Double a = Double.Parse(point / list.Count()+"");

            return Math.Round(a, 1);

        }
        ///


        [HttpPost]
        public double InsertNewVote(int songid,string username,double point)
        {
            DBMusicDataContext db = new DBMusicDataContext();
            vote v = new vote();
            double p;
             v= db.votes.FirstOrDefault(x => x.username == username && x.songid==songid);
            if (v != null) return 0;
            try
            {
               v = new vote();
                v.songid = songid;
                v.username = username;
                v.point = point;
                db.votes.InsertOnSubmit(v);
                db.SubmitChanges();
                
            }
            catch
            {
                return 0;
            }
            p = Point(songid);
            return p;
           
        }

        [HttpPost]
        public bool InsertComment(int songid, string username, String comment)
        {
            try
            {
                DBMusicDataContext db = new DBMusicDataContext();
               comment v = new comment();
                v.songid = songid;
                v.username = username;
                v.content = comment;


                db.comments.InsertOnSubmit(v);
                db.SubmitChanges();
                return true;
            }
            catch
            {
                return false;
            }
        }
    }
}
