package com.acme.video.service;

import java.util.ArrayList;
import java.util.List;

import com.acme.video.data.model.Episode;

public class EpisodeService extends AbstractService {

	public List<Episode> getEpisodes(String seasonId){
		List<Episode> episodesList = new ArrayList<>();
		Episode episode1 = new Episode();
		episode1.setRating(1);
		episodesList.add(episode1);
		Episode episode2 = new Episode();
		episode2.setRating(2);
		episodesList.add(episode2);
		return episodesList;
	}
}
