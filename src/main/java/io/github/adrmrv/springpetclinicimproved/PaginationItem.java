package io.github.adrmrv.springpetclinicimproved;

import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaginationItem {
	String displayName;
	String htmlClass;
	String url;
	
	public static List<PaginationItem> of(int curPage, int pageSize, int itemCount, String url) {
		List<PaginationItem> res = new ArrayList<PaginationItem>();
		
		res.add(new PaginationItem("Prev.", curPage > 1 ? "" : "disabled", url + "&p=" + (curPage-1)));

		if(curPage > 3) {
			res.add(new PaginationItem("1", "", url + "&p=1"));
		}

		if(curPage > 4) {
			res.add(new PaginationItem("2", "", url + "&p=2"));
		}

		if(curPage > 5) {
			res.add(new PaginationItem("...", "disabled", ""));
		}
		
		int lastPage = 1 + Math.floorDiv(itemCount-1, pageSize);
		
		for(Integer p = curPage - 2 ; p <= curPage+2 ; ++p) {
			if(p > 0 && p <= lastPage) {
				res.add(new PaginationItem(p.toString(), p == curPage ? "disabled" : "", url + "&p=" + p));
			}
		}


		if(curPage + 2 < lastPage - 2) {
			res.add(new PaginationItem("...", "disabled", ""));
		}

		if(curPage + 2 < lastPage - 1) {
			res.add(new PaginationItem(String.valueOf(lastPage - 1), "", url + "&p=" + (lastPage-1)));
		}

		if(curPage + 2 < lastPage) {
			res.add(new PaginationItem(String.valueOf(lastPage), "", url + "&p=" + lastPage));
		}

		res.add(new PaginationItem("Next", curPage < lastPage ? "" : "disabled", url + "&p=" + (curPage + 1)));		
		
		return res;
	}
}
