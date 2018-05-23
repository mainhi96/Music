using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;

namespace Music.Controller
{
    public class songController : ApiController
    {
        [HttpGet]
        public List<song> GetSongLists()
        {
            DBMusicDataContext db = new DBMusicDataContext();
            return db.songs.ToList();
        }
        ///

        [HttpGet]
        public song GetSongById(int id)
        {
            DBMusicDataContext db = new DBMusicDataContext();
            return db.songs.FirstOrDefault(x => x.id == id);
        }
        ///

        [HttpGet]
        public song GetSongByName(String name)
        {
            DBMusicDataContext db = new DBMusicDataContext();
            return db.songs.FirstOrDefault(x => x.name == name);
        }
        [HttpGet]
        public List<song> GetSongBySinger(String singer)
        {
            DBMusicDataContext db = new DBMusicDataContext();
            return db.songs.Where(x => x.singer == singer).ToList();


        }

 
    }
}

