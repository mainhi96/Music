using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;

namespace Music.Controller
{
    public class playlistController : ApiController
    {
        [HttpGet]
        public List<playlistinfo> GetPlayLists()
        {
            DBMusicDataContext db = new DBMusicDataContext();
            return db.playlistinfos.ToList();
        }
        ///
        public List<playlistsong> GetAllSong(int x)
        {
            DBMusicDataContext db = new DBMusicDataContext();
            return db.playlistsongs.ToList();
        }
        [HttpGet]
        public List<playlistsong> GetPlayListsSong(int playlistid)
        {
            DBMusicDataContext db = new DBMusicDataContext();
            return db.playlistsongs.Where(x => x.playlistid == playlistid).ToList();
        }

        [HttpGet]
        public List<playlistinfo> Getplaylistinfo(String username)
        {
            DBMusicDataContext db = new DBMusicDataContext();
            return db.playlistinfos.Where(x => x.username == username).ToList();
        }
        ///


        [HttpPost]
        public int InsertNewPlaylist(string name, string user)
        {
            try
            {
                DBMusicDataContext db = new DBMusicDataContext();
                playlistinfo playlist = new playlistinfo();
                playlistinfo playlistx = db.playlistinfos.FirstOrDefault(x => x.name == name && x.username==user );
                if (playlistx != null) return 0;//không tồn tại false
                playlist.username = user;
                playlist.name = name;
                db.playlistinfos.InsertOnSubmit(playlist);
                db.SubmitChanges();
                return playlist.id;
            }
            catch
            {
                return 0;
            }
        }
        ///

        [HttpPost]
        public Boolean InsertNewPlaylistSong(int playlistid, int songid)
        {
            try
            {
                DBMusicDataContext db = new DBMusicDataContext();
                playlistsong playlist = new playlistsong();
                playlist.playlistid = playlistid;
                playlist.songid = songid;
                db.playlistsongs.InsertOnSubmit(playlist);
                db.SubmitChanges();
                return true;
            }
            catch
            {
                return false;
            }
        }
        [HttpPut]
        public bool ChangeName(String name, int id)
        {
            try
            {
                DBMusicDataContext db = new DBMusicDataContext();
                playlistinfo playlist = db.playlistinfos.FirstOrDefault(x => x.id== id);
                if (playlist == null) return false;//không tồn tại false
                playlist.name = name;
                db.SubmitChanges();//xác nhận chỉnh sửa
                return true;
            }
            catch
            {
                return false;
            }
        }

        [HttpDelete]
        public bool DeletePlaylistSong(int playlist, int song)
        {
            DBMusicDataContext db = new DBMusicDataContext();
            playlistsong songplay = db.playlistsongs.FirstOrDefault(x => x.playlistid == playlist&&x.songid==song);
            if (songplay == null) return false;//không tồn tại false
            db.playlistsongs.DeleteOnSubmit(songplay);
            db.SubmitChanges();
            return true;
        }

        [HttpDelete]
        public bool DeletePlaylist(int id)
        {
            DBMusicDataContext db = new DBMusicDataContext();
            playlistinfo playlist = db.playlistinfos.FirstOrDefault(x => x.id == id);
            if (playlist == null) return false;//không tồn tại false
            List<playlistsong> listsong = GetPlayListsSong(id);
          foreach(playlistsong s in listsong)
            {
                DeletePlaylistSong(id, s.songid);
            }
          
           
            db.playlistinfos.DeleteOnSubmit(playlist);
            db.SubmitChanges();

           

            return true;
        }
    }
}
