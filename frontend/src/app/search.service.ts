import {Injectable} from '@angular/core';
import {MessageService} from './message.service';
import {Observable, of} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {catchError, tap} from 'rxjs/operators';
import {SearchResponse} from './search.response';
import {SuggestResponse} from './suggest.response';


@Injectable({
  providedIn: 'root'
})
export class SearchService {

  private searchUrl = 'http://localhost:8080/search';
  private suggestUrl = 'http://localhost:8080/suggest';

  constructor(private http: HttpClient,
              private messageService: MessageService) {
  }

  /* GET PDF links whose content contains search term */
  search(term: string): Observable<SearchResponse[]> {
    if (!term.trim()) {
      // if not search term, return empty hero array.
      return of([]);
    }
    return this.http.get<SearchResponse[]>(`${this.searchUrl}?phrase=${term}`).pipe(
      tap((response: SearchResponse[]) => response.forEach(res => this.log(`Result found: ${res.version}, ${res.fileName}, ${res.url}`))),
      catchError(this.handleError<SearchResponse[]>('search', []))
    );
  }

  /* GET PDF links and chapter title whose subtitles contains search term */
  suggest(term: string): Observable<SuggestResponse[]> {
    if (!term.trim()) {
      // if not search term, return empty hero array.
      return of([]);
    }
    return this.http.get<SuggestResponse[]>(`${this.suggestUrl}?phrase=${term}`).pipe(
      tap((response: SuggestResponse[]) => response.forEach(res => this.log(`Result found: ${res.version}, ${res.fileName}, ${res.url}, Chapter:${res.title}`))),
      catchError(this.handleError<SuggestResponse[]>('suggest', []))
    );
  }

  /**
   * Handle Http operation that failed.
   * Let the app continue.
   * @param operation - name of the operation that failed
   * @param result - optional value to return as the observable results
   */
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead

      // TODO: better job of transforming error for user consumption
      this.log(`${operation} failed: ${error.message}`);

      // Let the app keep running by returning an empty results.
      return of(result as T);
    };
  }

  private log(message: string) {
    this.messageService.add(`SearchService: ${message}`);
  }
}
